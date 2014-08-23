/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package org.madsonic.service;

import org.madsonic.Logger;
import org.madsonic.dao.AlbumDao;
import org.madsonic.dao.ArtistDao;
import org.madsonic.domain.Album;
import org.madsonic.domain.Artist;
import org.madsonic.domain.GenreSearchCriteria;
import org.madsonic.domain.MediaFile;
import org.madsonic.domain.MoodsSearchCriteria;
import org.madsonic.domain.MultiSearchCriteria;
import org.madsonic.domain.MusicFolder;
import org.madsonic.domain.RandomSearchCriteria;
import org.madsonic.domain.SearchCriteria;
import org.madsonic.domain.SearchResult;
import org.madsonic.util.FileUtil;
import org.madsonic.util.StringUtil;

import org.apache.lucene.analysis.ASCIIFoldingFilter;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.jfree.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.madsonic.service.SearchService.IndexType.*;

/**
 * Performs Lucene-based searching and indexing.
 *
 * @author Sindre Mehus
 * @version $Id: SearchService.java 3003 2012-06-07 09:19:44Z sindre_mehus $
 * @see MediaScannerService
 */
public class SearchService {

    private static final Logger LOG = Logger.getLogger(SearchService.class);

    private static final String FIELD_ID = "id";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_ALBUM = "album";
    private static final String FIELD_ARTIST = "artist";
    private static final String FIELD_GENRE = "genre";
    private static final String FIELD_MOOD = "mood";
    private static final String FIELD_YEAR = "year";
    private static final String FIELD_MEDIA_TYPE = "mediaType";
    private static final String FIELD_FOLDER = "folder";

    private static final Version LUCENE_VERSION = Version.LUCENE_30;

    private MediaFileService mediaFileService;
    private SettingsService settingsService;
    private ArtistDao artistDao;
    private AlbumDao albumDao;

    private IndexWriter artistWriter;
    private IndexWriter artistId3Writer;
    private IndexWriter albumWriter;
    private IndexWriter albumId3Writer;
    private IndexWriter songWriter;

    public SearchService() {
        removeLocks();
    }


    public void startIndexing() {
        try {
            artistWriter = createIndexWriter(ARTIST);
            artistId3Writer = createIndexWriter(ARTIST_ID3);
            albumWriter = createIndexWriter(ALBUM);
            albumId3Writer = createIndexWriter(ALBUM_ID3);
            songWriter = createIndexWriter(SONG);
        } catch (Exception x) {
            LOG.error("Failed to create search index.", x);
        }
    }

    public void index(MediaFile mediaFile) {
        try {
            if (mediaFile.isFile()) {
                songWriter.addDocument(SONG.createDocument(mediaFile));
            } else if (mediaFile.isAlbum()) {
                albumWriter.addDocument(ALBUM.createDocument(mediaFile));
            } else {
                artistWriter.addDocument(ARTIST.createDocument(mediaFile));
            }
        } catch (Exception x) {
            LOG.error("Failed to create search index for " + mediaFile, x);
        }
    }

    public void index(Artist artist) {
        try {
            artistId3Writer.addDocument(ARTIST_ID3.createDocument(artist));
        } catch (Exception x) {
            LOG.error("Failed to create search index for " + artist, x);
        }
    }

    public void index(Album album) {
        try {
            albumId3Writer.addDocument(ALBUM_ID3.createDocument(album));
        } catch (Exception x) {
            LOG.error("Failed to create search index for " + album, x);
        }
    }

    public void stopIndexing() {
        try {
            artistWriter.optimize();
            artistId3Writer.optimize();
            albumWriter.optimize();
            albumId3Writer.optimize();
            songWriter.optimize();
        } catch (Exception x) {
            LOG.error("Failed to create search index.", x);
        } finally {
            FileUtil.closeQuietly(artistId3Writer);
            FileUtil.closeQuietly(artistWriter);
            FileUtil.closeQuietly(albumWriter);
            FileUtil.closeQuietly(albumId3Writer);
            FileUtil.closeQuietly(songWriter);
        }
    }

    public SearchResult search(SearchCriteria criteria, IndexType indexType, int user_group_id) {
        SearchResult result = new SearchResult();
        int offset = criteria.getOffset();
        int count = criteria.getCount();
        result.setOffset(offset);

        IndexReader reader = null;
        try {
            reader = createIndexReader(indexType);
            Searcher searcher = new IndexSearcher(reader);
            Analyzer analyzer = new SubsonicAnalyzer();

            MultiFieldQueryParser queryParser = new MultiFieldQueryParser(LUCENE_VERSION, indexType.getFields(), analyzer, indexType.getBoosts());
			queryParser.setAllowLeadingWildcard(true);
			
            Query query = queryParser.parse(criteria.getQuery());
//          Query query = queryParser.parse(queryParser.escape(criteria.getQuery()));

            TopDocs topDocs = searcher.search(query, null, offset + count);
            result.setTotalHits(topDocs.totalHits);

            int start = Math.min(offset, topDocs.totalHits);
            int end = Math.min(start + count, topDocs.totalHits);
            for (int i = start; i < end; i++) {
                Document doc = searcher.doc(topDocs.scoreDocs[i].doc);
                switch (indexType) {
                    case SONG:
                    case ARTIST:
                    case ALBUM:
                        MediaFile mediaFile = mediaFileService.getMediaFile(Integer.valueOf(doc.get(FIELD_ID)), user_group_id);
                        addIfNotNull(mediaFile, result.getMediaFiles());
                        break;
                    case ARTIST_ID3:
                        Artist artist = artistDao.getArtist(Integer.valueOf(doc.get(FIELD_ID)));
                        addIfNotNull(artist, result.getArtists());
                        break;
                    case ALBUM_ID3:
                        Album album = albumDao.getAlbum(Integer.valueOf(doc.get(FIELD_ID)));
                        addIfNotNull(album, result.getAlbums());
                        break;
                    default:
                        break;
                }
            }

        } catch (Throwable x) {
            LOG.error("Failed to execute Lucene search.", x);
        } finally {
            FileUtil.closeQuietly(reader);
        }
        return result;
    }

    
//TODO:
    public List<MediaFile> getRandomSongs(MultiSearchCriteria criteria) {
    	
        List<MediaFile> result = new ArrayList<MediaFile>();
        String musicFolderPath = null;
        
        if (criteria.getMusicFolderId() != null && criteria.getUserGroupId() == null) {
            MusicFolder musicFolder = settingsService.getMusicFolderById(criteria.getMusicFolderId());
            musicFolderPath = musicFolder.getPath().getPath();
        }

        ArrayList<String> musicFolderPaths = new ArrayList<String>();       
        List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(criteria.getUserGroupId(), settingsService.isSortMediaFileFolder());
 
    	for (MusicFolder musicFolder : musicFolders) {
    		if (musicFolder != null) {
                musicFolderPaths.add(musicFolder.getPath().getPath());
			}
    	}
        
        IndexReader reader = null;
        try {
            reader = createIndexReader(SONG);
            Searcher searcher = new IndexSearcher(reader);

            BooleanQuery queryMaster    = new BooleanQuery();
            
            BooleanQuery queryArtist    = new BooleanQuery();
            BooleanQuery queryAlbum     = new BooleanQuery();
            BooleanQuery queryMediaType = new BooleanQuery();
            BooleanQuery queryGenre     = new BooleanQuery();
            BooleanQuery queryMood      = new BooleanQuery();
            BooleanQuery queryYear      = new BooleanQuery();
            BooleanQuery queryFolder    = new BooleanQuery();
            
            queryMediaType.add(new TermQuery(new Term(FIELD_MEDIA_TYPE, MediaFile.MediaType.MUSIC.name().toLowerCase())), BooleanClause.Occur.SHOULD);

            if (criteria.getArtists() != null) {
				for (String _art : criteria.getArtists()) {
				   List<String> allArtistTerms = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(_art," ")));
				   for (String artistTerm : allArtistTerms ) {
				        queryArtist.add(new TermQuery(new Term(FIELD_ARTIST, artistTerm.toLowerCase() ) ), BooleanClause.Occur.SHOULD);
				   } 
				}
            }
            
            if (criteria.getAlbums() != null) {
				for (String _album : criteria.getAlbums()) {
				   List<String> allAlbumTerms = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(_album," ")));
				   for (String albumTerm : allAlbumTerms ) {
					   queryAlbum.add(new TermQuery(new Term(FIELD_ALBUM, albumTerm.toLowerCase() ) ), BooleanClause.Occur.SHOULD);
				   } 
				}
            }
            
            if (criteria.getGenres() != null) {
				for (String _genre : criteria.getGenres()) {
				   List<String> allGenreTerms = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(_genre," ")));
				   for (String genreTerm : allGenreTerms ) {
	            		if (genreTerm != null) {
	            			queryGenre.add(new TermQuery(new Term(FIELD_GENRE, genreTerm.toLowerCase() ) ), BooleanClause.Occur.SHOULD);
	            		}
				   } 
				}
            }
            
            if (criteria.getMoods() != null) {
				for (String _mood : criteria.getMoods()) {
				   List<String> allMoodsTerms = new LinkedList<String>(Arrays.asList(StringUtil.splitMoods(_mood," ")));
				   for (String moodTerm : allMoodsTerms ) {
	            		if (moodTerm != null) {
	            			queryMood.add(new TermQuery(new Term(FIELD_MOOD, moodTerm.toLowerCase() ) ), BooleanClause.Occur.SHOULD);
	            		}
				   } 
				}
            }
            
            if (criteria.getFromYear() != null || criteria.getToYear() != null) {
                NumericRangeQuery<Integer> rangeQuery = NumericRangeQuery.newIntRange(FIELD_YEAR, criteria.getFromYear(), criteria.getToYear(), true, true);
                queryYear.add(rangeQuery, BooleanClause.Occur.SHOULD);
            }
            if (musicFolderPath != null) {
            	queryFolder.add(new TermQuery(new Term(FIELD_FOLDER, musicFolderPath)), BooleanClause.Occur.SHOULD);
            }

            if (musicFolderPaths != null) {
	        	for (int i = 0; i < musicFolderPaths.size(); i++) { 
	               	queryFolder.add(new TermQuery(new Term(FIELD_FOLDER, musicFolderPaths.get(i))), BooleanClause.Occur.SHOULD);
	        	}
            }
       
            queryMaster.add(queryMediaType, BooleanClause.Occur.MUST);
            
            if (criteria.getArtists() != null) {
            	queryMaster.add(queryArtist, BooleanClause.Occur.MUST);
            }
            if (criteria.getAlbums() != null) {
            	queryMaster.add(queryAlbum, BooleanClause.Occur.MUST);
            }

            if (criteria.getGenres() != null) {
            	queryMaster.add(queryGenre, BooleanClause.Occur.SHOULD);
            }

            if (criteria.getMoods() != null) {
            	queryMaster.add(queryMood, BooleanClause.Occur.SHOULD);
            }
            
            if (criteria.getFromYear() != null) {
            	
            	queryMaster.add(queryYear, BooleanClause.Occur.SHOULD);
            	
            }
        	queryMaster.add(queryFolder, BooleanClause.Occur.MUST);
        
            TopDocs topDocs = searcher.search(queryMaster, null, Integer.MAX_VALUE);
            Random random = new Random(System.currentTimeMillis());

            for (int i = 0; i < Math.min(criteria.getCount(), topDocs.totalHits); i++) {
                int index = random.nextInt(topDocs.totalHits);
                Document doc = searcher.doc(topDocs.scoreDocs[index].doc);
                int id = Integer.valueOf(doc.get(FIELD_ID));
                try {
                    result.add(mediaFileService.getMediaFile(id)); 
                } catch (Exception x) {
                    LOG.warn("Failed to get media file " + id);
                }
            }

        } catch (Throwable x) {
            LOG.error("Failed to search or random songs.", x);
        } finally {
            FileUtil.closeQuietly(reader);
        }
        
        // Filter out duplicates
         HashSet<MediaFile> hs = new HashSet<MediaFile>();
	     hs.addAll(result);
	     result.clear();
	     result.addAll(hs);
        
        return result;
        }
    
    /**
     * Returns a number of random songs.
     *
     * @param criteria Search criteria.
     * @return List of random songs.
     */
    public List<MediaFile> getRandomSongs(RandomSearchCriteria criteria) {
        List<MediaFile> result = new ArrayList<MediaFile>();

        String musicFolderPath = null;
        if (criteria.getMusicFolderId() != null) {
            MusicFolder musicFolder = settingsService.getMusicFolderById(criteria.getMusicFolderId());
            musicFolderPath = musicFolder.getPath().getPath();
        }

        IndexReader reader = null;
        try {
            reader = createIndexReader(SONG);
            Searcher searcher = new IndexSearcher(reader);

            BooleanQuery query = new BooleanQuery();
            query.add(new TermQuery(new Term(FIELD_MEDIA_TYPE, MediaFile.MediaType.MUSIC.name().toLowerCase())), BooleanClause.Occur.MUST);
            if (criteria.getGenre() != null) {
                String genre = normalize(criteria.getGenre());
                query.add(new TermQuery(new Term(FIELD_GENRE, genre)), BooleanClause.Occur.MUST);
            }
            if (criteria.getFromYear() != null || criteria.getToYear() != null) {
                NumericRangeQuery<Integer> rangeQuery = NumericRangeQuery.newIntRange(FIELD_YEAR, criteria.getFromYear(), criteria.getToYear(), true, true);
                query.add(rangeQuery, BooleanClause.Occur.MUST);
            }
            if (musicFolderPath != null) {
                query.add(new TermQuery(new Term(FIELD_FOLDER, musicFolderPath)), BooleanClause.Occur.MUST);
            }

            TopDocs topDocs = searcher.search(query, null, Integer.MAX_VALUE);
            Random random = new Random(System.currentTimeMillis());

            for (int i = 0; i < Math.min(criteria.getCount(), topDocs.totalHits); i++) {
                int index = random.nextInt(topDocs.totalHits);
                Document doc = searcher.doc(topDocs.scoreDocs[index].doc);
                int id = Integer.valueOf(doc.get(FIELD_ID));
                try {
                    result.add(mediaFileService.getMediaFile(id));
                } catch (Exception x) {
                    LOG.warn("Failed to get media file " + id);
                }
            }

        } catch (Throwable x) {
            LOG.error("Failed to search or random songs.", x);
        } finally {
            FileUtil.closeQuietly(reader);
        }
        return result;
    }

    public static String normalize(String genre) {
    	String tmp = genre.toLowerCase();
    	tmp = tmp.replace(" ", "");
    	tmp = tmp.replace(";", "");
    	tmp = tmp.replace(",", "");
    	tmp = tmp.replace("-", "");
    	tmp = tmp.replace("/", "");
      	tmp = tmp.replace("[", "");
      	tmp = tmp.replace("]", "");
      	tmp = tmp.replace("+", "");
      	tmp = tmp.replace(" ", "");
    	return tmp;
    }

    /**
     * Returns a number of random songs.
     *
     * @param criteria Search criteria.
     * @return List of random songs.
     */
    public List<MediaFile> getRandomSongs(GenreSearchCriteria criteria) {
        List<MediaFile> result = new ArrayList<MediaFile>();

        String musicFolderPath = null;
        
        if (criteria.getMusicFolderId() != null && criteria.getUserGroupId() == null) {
            MusicFolder musicFolder = settingsService.getMusicFolderById(criteria.getMusicFolderId());
            musicFolderPath = musicFolder.getPath().getPath();
        }

//        if (criteria.getUserGroupId() != null){
//        	List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(criteria.getUserGroupId());
//        	Random generator = new Random();
//        	int index = generator.nextInt (musicFolders.size());
//        	if ( index >-1 ) { 
//        		MusicFolder musicFolder = settingsService.getMusicFolderById(musicFolders.get(index).getId());
//                musicFolderPath = musicFolder.getPath().getPath();
//        		}
//        }

        ArrayList<String> musicFolderPaths = new ArrayList<String>();       
        List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(criteria.getUserGroupId(), settingsService.isSortMediaFileFolder());
 
    	for (MusicFolder musicFolder : musicFolders) {
    		if (musicFolder != null) {
                musicFolderPaths.add(musicFolder.getPath().getPath());
			}
    	}
        
        IndexReader reader = null;
        try {
            reader = createIndexReader(SONG);
            Searcher searcher = new IndexSearcher(reader);

            BooleanQuery queryMaster = new BooleanQuery();
            BooleanQuery queryMediaType = new BooleanQuery();
            BooleanQuery queryGenre = new BooleanQuery();
            BooleanQuery queryYear = new BooleanQuery();
            BooleanQuery queryFolder = new BooleanQuery();
            
            queryMediaType.add(new TermQuery(new Term(FIELD_MEDIA_TYPE, MediaFile.MediaType.MUSIC.name().toLowerCase())), BooleanClause.Occur.SHOULD);
            queryMediaType.add(new TermQuery(new Term(FIELD_MEDIA_TYPE, MediaFile.MediaType.PODCAST.name().toLowerCase())), BooleanClause.Occur.SHOULD);
            
            if (criteria.getGenre() != null) {
				String genre = normalize(criteria.getGenre());
            	queryGenre.add(new TermQuery(new Term(FIELD_GENRE, genre)), BooleanClause.Occur.SHOULD);
            }
            
        	if (criteria.getGenres() != null) {
            	for (String genre : criteria.getGenres()) {
            		if (genre != null) {
						queryGenre.add(new TermQuery(new Term(FIELD_GENRE, normalize(genre))), BooleanClause.Occur.SHOULD);
            			}
                }
            }
       
            if (criteria.getFromYear() != null || criteria.getToYear() != null) {
                NumericRangeQuery<Integer> rangeQuery = NumericRangeQuery.newIntRange(FIELD_YEAR, criteria.getFromYear(), criteria.getToYear(), true, true);
                queryYear.add(rangeQuery, BooleanClause.Occur.SHOULD);
            }
            
            if (musicFolderPath != null) {
            	queryFolder.add(new TermQuery(new Term(FIELD_FOLDER, musicFolderPath)), BooleanClause.Occur.SHOULD);
            }

            if (musicFolderPaths != null) {
	        	for (int i = 0; i < musicFolderPaths.size(); i++) { 
	               	queryFolder.add(new TermQuery(new Term(FIELD_FOLDER, musicFolderPaths.get(i))), BooleanClause.Occur.SHOULD);
	        	}
            }
       
        	queryMaster.add(queryMediaType, BooleanClause.Occur.SHOULD);
        	queryMaster.add(queryGenre, BooleanClause.Occur.MUST);
            if (criteria.getFromYear() != null || criteria.getToYear() != null) {
            	queryMaster.add(queryYear, BooleanClause.Occur.MUST);
            } else {
            	queryMaster.add(queryYear, BooleanClause.Occur.SHOULD);
            }
        	queryMaster.add(queryFolder, BooleanClause.Occur.MUST);
        
            TopDocs topDocs = searcher.search(queryMaster, null, Integer.MAX_VALUE);
            Random random = new Random(System.currentTimeMillis());

            for (int i = 0; i < Math.min(criteria.getCount(), topDocs.totalHits); i++) {
                int index = random.nextInt(topDocs.totalHits);
                Document doc = searcher.doc(topDocs.scoreDocs[index].doc);
                int id = Integer.valueOf(doc.get(FIELD_ID));
                try {
                    result.add(mediaFileService.getMediaFile(id));
                } catch (Exception x) {
                    LOG.warn("Failed to get media file " + id);
                }
            }

        } catch (Throwable x) {
            LOG.error("Failed to search or random songs.", x);
        } finally {
            FileUtil.closeQuietly(reader);
        }
        
        // Filter out duplicates
         HashSet<MediaFile> hs = new HashSet<MediaFile>();
	     hs.addAll(result);
	     result.clear();
	     result.addAll(hs);
        
        return result;
    }
   

    public List<MediaFile> getRandomSongs(MoodsSearchCriteria criteria) {
        List<MediaFile> result = new ArrayList<MediaFile>();

        String musicFolderPath = null;
        
        if (criteria.getMusicFolderId() != null && criteria.getUserGroupId() == null) {
            MusicFolder musicFolder = settingsService.getMusicFolderById(criteria.getMusicFolderId());
            musicFolderPath = musicFolder.getPath().getPath();
        }
        ArrayList<String> musicFolderPaths = new ArrayList<String>();       
        List<MusicFolder> musicFolders = settingsService.getAllMusicFolders(criteria.getUserGroupId(), settingsService.isSortMediaFileFolder());
 
    	for (MusicFolder musicFolder : musicFolders) {
    		if (musicFolder != null) {
                musicFolderPaths.add(musicFolder.getPath().getPath());
			}
    	}
        
        IndexReader reader = null;
        try {
            reader = createIndexReader(SONG);
            Searcher searcher = new IndexSearcher(reader);

            BooleanQuery queryMaster = new BooleanQuery();
            BooleanQuery queryMediaType = new BooleanQuery();
            BooleanQuery queryMood = new BooleanQuery();
            BooleanQuery queryYear = new BooleanQuery();
            BooleanQuery queryFolder = new BooleanQuery();
            
            queryMediaType.add(new TermQuery(new Term(FIELD_MEDIA_TYPE, MediaFile.MediaType.MUSIC.name().toLowerCase())), BooleanClause.Occur.SHOULD);
            queryMediaType.add(new TermQuery(new Term(FIELD_MEDIA_TYPE, MediaFile.MediaType.PODCAST.name().toLowerCase())), BooleanClause.Occur.SHOULD);
            
            if (criteria.getMood() != null) {
				String mood = criteria.getMood();
				queryMood.add(new TermQuery(new Term(FIELD_MOOD, mood.toLowerCase())), BooleanClause.Occur.SHOULD);
            }
            
        	if (criteria.getMoods() != null) {
            	for (String mood : criteria.getMoods()) {
            		if (mood != null) {
            			queryMood.add(new TermQuery(new Term(FIELD_MOOD, mood.toLowerCase())), BooleanClause.Occur.SHOULD);
            			}
                }
            }
       
            if (criteria.getFromYear() != null || criteria.getToYear() != null) {
                NumericRangeQuery<Integer> rangeQuery = NumericRangeQuery.newIntRange(FIELD_YEAR, criteria.getFromYear(), criteria.getToYear(), true, true);
                queryYear.add(rangeQuery, BooleanClause.Occur.SHOULD);
            }
            if (musicFolderPath != null) {
            	queryFolder.add(new TermQuery(new Term(FIELD_FOLDER, musicFolderPath)), BooleanClause.Occur.SHOULD);
            }

            if (musicFolderPaths != null) {
	        	for (int i = 0; i < musicFolderPaths.size(); i++) { 
	               	queryFolder.add(new TermQuery(new Term(FIELD_FOLDER, musicFolderPaths.get(i))), BooleanClause.Occur.SHOULD);
	        	}
            }

        	queryMaster.add(queryMediaType, BooleanClause.Occur.SHOULD);
        	queryMaster.add(queryMood, BooleanClause.Occur.MUST);
        	
            if (criteria.getFromYear() != null || criteria.getToYear() != null) {
            	queryMaster.add(queryYear, BooleanClause.Occur.MUST);
            } else {
            	queryMaster.add(queryYear, BooleanClause.Occur.SHOULD);
            }
        	
        	queryMaster.add(queryFolder, BooleanClause.Occur.MUST);
        
            TopDocs topDocs = searcher.search(queryMaster, null, Integer.MAX_VALUE);
            Random random = new Random(System.currentTimeMillis());

            for (int i = 0; i < Math.min(criteria.getCount(), topDocs.totalHits); i++) {
                int index = random.nextInt(topDocs.totalHits);
                Document doc = searcher.doc(topDocs.scoreDocs[index].doc);
                int id = Integer.valueOf(doc.get(FIELD_ID));
                try {
                    result.add(mediaFileService.getMediaFile(id));
                } catch (Exception x) {
                    LOG.warn("Failed to get media file " + id);
                }
            }

        } catch (Throwable x) {
            LOG.error("Failed to search or random songs.", x);
        } finally {
            FileUtil.closeQuietly(reader);
        }
        
        // Filter out duplicates
         HashSet<MediaFile> hs = new HashSet<MediaFile>();
	     hs.addAll(result);
	     result.clear();
	     result.addAll(hs);
        
        return result;
    }
    
    
    
    /**
     * Returns a number of random albums.
     *
     * @param count Number of albums to return.
     * @return List of random albums.
     */
    public List<MediaFile> getRandomAlbums(MusicFolder musicFolder, int count, int user_group_id) {
         List<MediaFile> result = new ArrayList<MediaFile>();

        IndexReader reader = null;
        try {
            reader = createIndexReader(ALBUM);
            Searcher searcher = new IndexSearcher(reader);
            Query query = new MatchAllDocsQuery();
            TopDocs topDocs = searcher.search(query, null, Integer.MAX_VALUE);
            Random random = new Random(System.currentTimeMillis());
            
            int counter = 0;
            for (int i = 0; i < Math.min(count, topDocs.totalHits); i++) {
            	if (counter > 50) { break; }
            	
                int index = random.nextInt(topDocs.totalHits);
                Document doc = searcher.doc(topDocs.scoreDocs[index].doc);
                int id = Integer.valueOf(doc.get(FIELD_ID));
                String path = doc.get(FIELD_FOLDER);
                
                try {
                	if (musicFolder != null  ) {
                		if (path.equalsIgnoreCase(musicFolder.getPath().toString())) {
                    		if (addIfNotNull(mediaFileService.getMediaFile(id, user_group_id), result) == 0) {
                    			i--;
                    		}
                    	} else {
                			i--;
                		}
                	}
                	 else {
                		 
                		if (addIfNotNull(mediaFileService.getMediaFile(id, user_group_id), result) == 0) {
                			i--;
                		}
                	}
                }
                catch (Exception x) {
                    LOG.warn("Failed to get media file " + id, x);
                }
                counter++;
            }

        } catch (Throwable x) {
            LOG.error("Failed to search for random albums.", x);
        } finally {
            FileUtil.closeQuietly(reader);
        }
        return result;
    }

    /**
     * Returns a number of random albums, using ID3 tag.
     *
     * @param count Number of albums to return.
     * @return List of random albums.
     */
    public List<Album> getRandomAlbumsId3(int count) {
        List<Album> result = new ArrayList<Album>();

        IndexReader reader = null;
        try {
            reader = createIndexReader(ALBUM_ID3);
            Searcher searcher = new IndexSearcher(reader);

            Query query = new MatchAllDocsQuery();
            TopDocs topDocs = searcher.search(query, null, Integer.MAX_VALUE);
            Random random = new Random(System.currentTimeMillis());

            for (int i = 0; i < Math.min(count, topDocs.totalHits); i++) {
                int index = random.nextInt(topDocs.totalHits);
                Document doc = searcher.doc(topDocs.scoreDocs[index].doc);
                int id = Integer.valueOf(doc.get(FIELD_ID));
                try {
                    addIfNotNull(albumDao.getAlbum(id), result);
                } catch (Exception x) {
                    LOG.warn("Failed to get album file " + id, x);
                }
            }

        } catch (Throwable x) {
            LOG.error("Failed to search for random albums.", x);
        } finally {
            FileUtil.closeQuietly(reader);
        }
        return result;
    }

    private <T> int addIfNotNull(T value, List<T> list) {
        if (value != null) {
        	if (list.contains(value)) {
        		return 0;
        	} else {
            list.add(value);
        	}
        }
		return 1;
    }
    private IndexWriter createIndexWriter(IndexType indexType) throws IOException {
        File dir = getIndexDirectory(indexType);
        return new IndexWriter(FSDirectory.open(dir), new SubsonicAnalyzer(), true, new IndexWriter.MaxFieldLength(10));
    }

    private IndexReader createIndexReader(IndexType indexType) throws IOException {
        File dir = getIndexDirectory(indexType);
        return IndexReader.open(FSDirectory.open(dir), true);
    }

    private File getIndexRootDirectory() {
        return new File(SettingsService.getMadsonicHome(), "lucene2");
    }

    private File getIndexDirectory(IndexType indexType) {
        return new File(getIndexRootDirectory(), indexType.toString().toLowerCase());
    }

    private void removeLocks() {
        for (IndexType indexType : IndexType.values()) {
            Directory dir = null;
            try {
                dir = FSDirectory.open(getIndexDirectory(indexType));
                if (IndexWriter.isLocked(dir)) {
                    IndexWriter.unlock(dir);
                    LOG.info("Removed Lucene lock file in " + dir);
                }
            } catch (Exception x) {
                LOG.warn("Failed to remove Lucene lock file in " + dir, x);
            } finally {
                FileUtil.closeQuietly(dir);
            }
        }
    }

    public void setMediaFileService(MediaFileService mediaFileService) {
        this.mediaFileService = mediaFileService;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public void setArtistDao(ArtistDao artistDao) {
        this.artistDao = artistDao;
    }

    public void setAlbumDao(AlbumDao albumDao) {
        this.albumDao = albumDao;
    }

    public static enum IndexType {

        SONG(new String[]{FIELD_TITLE, FIELD_ARTIST}, FIELD_TITLE) {
            @Override
            public Document createDocument(MediaFile mediaFile) {
                Document doc = new Document();
                doc.add(new NumericField(FIELD_ID, Field.Store.YES, false).setIntValue(mediaFile.getId()));
                doc.add(new Field(FIELD_MEDIA_TYPE, mediaFile.getMediaType().name(), Field.Store.NO, Field.Index.ANALYZED_NO_NORMS));

                if (mediaFile.getTitle() != null) {
                    doc.add(new Field(FIELD_TITLE, mediaFile.getTitle(), Field.Store.YES, Field.Index.ANALYZED));
                }
                if (mediaFile.getArtist() != null) {
                    doc.add(new Field(FIELD_ARTIST, mediaFile.getArtist(), Field.Store.YES, Field.Index.ANALYZED));
                }
                if (mediaFile.getAlbumName() != null) {
                    doc.add(new Field(FIELD_ALBUM, mediaFile.getAlbumName(), Field.Store.YES, Field.Index.ANALYZED));
                }
                if (mediaFile.getGenre() != null) {
                    doc.add(new Field(FIELD_GENRE, normalize(mediaFile.getGenre()), Field.Store.NO, Field.Index.ANALYZED));
                }
                if (mediaFile.getMood() != null) {
                    doc.add(new Field(FIELD_MOOD, mediaFile.getMood(), Field.Store.NO, Field.Index.ANALYZED));
                }
                if (mediaFile.getYear() != null) {
                    doc.add(new NumericField(FIELD_YEAR, Field.Store.NO, true).setIntValue(mediaFile.getYear()));
                }
                if (mediaFile.getFolder() != null) {
                    doc.add(new Field(FIELD_FOLDER, mediaFile.getFolder(), Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
                }

                return doc;
            }
        },

        ALBUM(new String[]{FIELD_ALBUM, FIELD_ARTIST}, FIELD_ALBUM) {
            @Override
            public Document createDocument(MediaFile mediaFile) {
                Document doc = new Document();
                doc.add(new NumericField(FIELD_ID, Field.Store.YES, false).setIntValue(mediaFile.getId()));

                if (mediaFile.getArtist() != null) {
                    doc.add(new Field(FIELD_ARTIST, mediaFile.getArtist(), Field.Store.YES, Field.Index.ANALYZED));
                }
                if (mediaFile.getAlbumName() != null) {
                    doc.add(new Field(FIELD_ALBUM, mediaFile.getAlbumName(), Field.Store.YES, Field.Index.ANALYZED));
                }
                if (mediaFile.getFolder() != null) {
                    doc.add(new Field(FIELD_FOLDER, mediaFile.getFolder(), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
                }
                return doc;
            }
        },

        ALBUM_ID3(new String[]{FIELD_ALBUM, FIELD_ARTIST}, FIELD_ALBUM) {
            @Override
            public Document createDocument(Album album) {
                Document doc = new Document();
                doc.add(new NumericField(FIELD_ID, Field.Store.YES, false).setIntValue(album.getId()));

                if (album.getArtist() != null) {
                    doc.add(new Field(FIELD_ARTIST, album.getArtist(), Field.Store.YES, Field.Index.ANALYZED));
                }
                if (album.getName() != null) {
                    doc.add(new Field(FIELD_ALBUM, album.getName(), Field.Store.YES, Field.Index.ANALYZED));
                }

                return doc;
            }
        },

        ARTIST(new String[]{FIELD_ARTIST}, null) {
            @Override
            public Document createDocument(MediaFile mediaFile) {
                Document doc = new Document();
                doc.add(new NumericField(FIELD_ID, Field.Store.YES, false).setIntValue(mediaFile.getId()));

                if (mediaFile.getArtist() != null) {
                    doc.add(new Field(FIELD_ARTIST, mediaFile.getArtist(), Field.Store.YES, Field.Index.ANALYZED));
                }

                return doc;
            }
        },

        ARTIST_ID3(new String[]{FIELD_ARTIST}, null) {
            @Override
            public Document createDocument(Artist artist) {
                Document doc = new Document();
                doc.add(new NumericField(FIELD_ID, Field.Store.YES, false).setIntValue(artist.getId()));
                doc.add(new Field(FIELD_ARTIST, artist.getName(), Field.Store.YES, Field.Index.ANALYZED));

                return doc;
            }
        };

        private final String[] fields;
        private final Map<String, Float> boosts;

        private IndexType(String[] fields, String boostedField) {
            this.fields = fields;
            boosts = new HashMap<String, Float>();
            if (boostedField != null) {
                boosts.put(boostedField, 2.0F);
            }
        }

        public String[] getFields() {
            return fields;
        }

        protected Document createDocument(MediaFile mediaFile) {
            throw new UnsupportedOperationException();
        }

        protected Document createDocument(Artist artist) {
            throw new UnsupportedOperationException();
        }

        protected Document createDocument(Album album) {
            throw new UnsupportedOperationException();
        }

        public Map<String, Float> getBoosts() {
            return boosts;
        }
    }

    private class SubsonicAnalyzer extends StandardAnalyzer {
        private SubsonicAnalyzer() {
            super(LUCENE_VERSION);
        }

        @Override
        public TokenStream tokenStream(String fieldName, Reader reader) {
            TokenStream result = super.tokenStream(fieldName, reader);
            return new ASCIIFoldingFilter(result);
        }

        @Override
        public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
            class SavedStreams {
                StandardTokenizer tokenStream;
                TokenStream filteredTokenStream;
            }

            SavedStreams streams = (SavedStreams) getPreviousTokenStream();
            if (streams == null) {
                streams = new SavedStreams();
                setPreviousTokenStream(streams);
                streams.tokenStream = new StandardTokenizer(LUCENE_VERSION, reader);
                streams.filteredTokenStream = new StandardFilter(streams.tokenStream);
                streams.filteredTokenStream = new LowerCaseFilter(streams.filteredTokenStream);
                streams.filteredTokenStream = new StopFilter(true, streams.filteredTokenStream, STOP_WORDS_SET);
                streams.filteredTokenStream = new ASCIIFoldingFilter(streams.filteredTokenStream);
            } else {
                streams.tokenStream.reset(reader);
            }
            streams.tokenStream.setMaxTokenLength(DEFAULT_MAX_TOKEN_LENGTH);

            return streams.filteredTokenStream;
        }
    }
}


