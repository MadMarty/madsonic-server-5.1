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
package org.madsonic.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import org.madsonic.Logger;
import org.madsonic.domain.Transcoding;

/**
 * Provides database services for transcoding configurations.
 *
 * @author Sindre Mehus
 */
public class TranscodingDao extends AbstractDao {

    private static final Logger LOG = Logger.getLogger(TranscodingDao.class);
    private static final String COLUMNS = "id, name, source_formats, target_format, step1, step2, step3, default_active";
    private TranscodingRowMapper rowMapper = new TranscodingRowMapper();

    /**
     * Returns all transcodings.
     *
     * @return Possibly empty list of all transcodings.
     */
    public List<Transcoding> getAllTranscodings() {
        String sql = "select " + COLUMNS + " from transcoding2";
        return query(sql, rowMapper);
    }

    /**
     * Returns all active transcodings for the given player.
     *
     * @param playerId The player ID.
     * @return All active transcodings for the player.
     */
    public List<Transcoding> getTranscodingsForPlayer(String playerId) {
        String sql = "select " + COLUMNS + " from transcoding2, player_transcoding2 " +
                "where player_transcoding2.player_id = ? " +
                "and   player_transcoding2.transcoding_id = transcoding2.id";
        return query(sql, rowMapper, playerId);
    }

    /**
     * Sets the list of active transcodings for the given player.
     *
     * @param playerId       The player ID.
     * @param transcodingIds ID's of the active transcodings.
     */
    public void setTranscodingsForPlayer(String playerId, int[] transcodingIds) {
        update("delete from player_transcoding2 where player_id = ?", playerId);
        String sql = "insert into player_transcoding2(player_id, transcoding_id) values (?, ?)";
        for (int transcodingId : transcodingIds) {
            update(sql, playerId, transcodingId);
        }
    }

    /**
     * Creates a new transcoding.
     *
     * @param transcoding The transcoding to create.
     */
    public synchronized void createTranscoding(Transcoding transcoding) {
        int id = getJdbcTemplate().queryForInt("select max(id) + 1 from transcoding2");
        transcoding.setId(id);
        String sql = "insert into transcoding2 (" + COLUMNS + ") values (" + questionMarks(COLUMNS) + ")";
        update(sql, transcoding.getId(), transcoding.getName(), transcoding.getSourceFormats(),
                transcoding.getTargetFormat(), transcoding.getStep1(),
                transcoding.getStep2(), transcoding.getStep3(), transcoding.isDefaultActive());
        LOG.info("Created transcoding " + transcoding.getName());
    }

    /**
     * Deletes the transcoding with the given ID.
     *
     * @param id The transcoding ID.
     */
    public void deleteTranscoding(Integer id) {
        String sql = "delete from transcoding2 where id=?";
        update(sql, id);
        LOG.info("Deleted transcoding with ID " + id);
    }
    
    public void reset2FLV() {
        update("delete from transcoding2");
        update("delete from player_transcoding2");
        update("insert into transcoding2(name, source_formats, target_format, step1) values('mp3 audio','ogg oga aac m4a flac wav wma aif aiff ape mpc shn', 'mp3', 'Audioffmpeg -i %s -ab %bk -v 0 -f mp3 -')");
        update("insert into transcoding2(name, source_formats, target_format, step1) values('flv/h264 video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'flv', 'ffmpeg -ss %o -i %s -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset superfast -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('wtv video', 'wtv', 'flv', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset fast -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('m4a/FLAC audio', 'm4a flac', 'mp3', 'Audioffmpeg -i %s -ab 320k -ar 44100 -ac 2 -v 0 -f mp3 -')");
	//	update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('SubWiji', 'mp3', 'mp3', 'Audioffmpeg -f mp3 -i %s -ab %bk -v 0 -f mp3 -', false)");
		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'SubWiji'");
        LOG.info("##Transcoding reseted to FLV defaults");
    }
    	    
    public void reset2WEBM() {
        update("delete from transcoding2");
        update("delete from player_transcoding2");
        update("insert into transcoding2(name, source_formats, target_format, step1) values('mp3 audio','ogg oga aac m4a flac wav wma aif aiff ape mpc shn', 'mp3', 'Audioffmpeg -i %s -ab %bk -v 0 -f mp3 -')");
        update("insert into transcoding2(name, source_formats, target_format, step1) values('flv/h264 video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts flv', 'webm', 'ffmpeg -ss %o -t %d -i %s -async 1 -vf lutyuv=y=val*1.3 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f webm -vcodec libvpx -preset superfast -acodec libvorbis -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('wtv video', 'wtv', 'webm', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset fast -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('m4a/FLAC audio', 'm4a flac', 'mp3', 'Audioffmpeg -i %s -ab 320k -ar 44100 -ac 2 -v 0 -f mp3 -')");
	//	update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('SubWiji', 'mp3', 'mp3', 'Audioffmpeg -f mp3 -i %s -ab %bk -v 0 -f mp3 -', false)");
		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'SubWiji'");
        LOG.info("##Transcoding reseted to WEBM defaults");
    }

    public void reset2Subsonic() {
        update("delete from transcoding2");
        update("delete from player_transcoding2");
        update("insert into transcoding2(name, source_formats, target_format, step1) values('mp3 audio','ogg oga aac m4a flac wav wma aif aiff ape mpc shn', 'mp3', 'ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -')");
        update("insert into transcoding2(name, source_formats, target_format, step1) values('flv/h264 video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'flv', 'ffmpeg -ss %o -i %s -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset superfast -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('mkv video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts flv', 'mkv', 'ffmpeg -ss %o -i %s -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");
		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'SubWiji'");
        LOG.info("##Transcoding reseted to Subsonic defaults");
    }    

    public void reset2MadsonicDefault() {
        update("delete from transcoding2");
        update("delete from player_transcoding2");
        
        update("insert into transcoding2(name, source_formats, target_format, step1) values('video->flv', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'flv', 'ffmpeg -ss %o -i %s -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('video->mkv', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'mkv', 'ffmpeg -ss %o -i %s -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('video->webm', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'webm', 'ffmpeg -ss %o -t %d -i %s -async 1 -vf lutyuv=y=val*1.3 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f webm -c:v libvpx -preset superfast -c:a libvorbis -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('wtv->flv', 'wtv', 'flv', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s 720x360 -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset fast -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun->flv', 'hdrun', 'flv', 'ffmpeg -ss %o -i %z -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun->mkv', 'hdrun', 'mkv', 'ffmpeg -ss %o -i %z -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv->flv', 'tv', 'flv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv->mkv', 'tv', 'mkv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('audio->ogg', 'aac wav wma aif aiff ape mp3 m4a mpc shn flac', 'ogg', 'ffmpeg -i %s -map 0:0 -vn -c:a libvorbis -ar 44100 -b:a %bk -v 0 -f ogg -', false)");		
		update("insert into transcoding2(name, source_formats, target_format, step1) values('audio->mp3', 'ogg oga aac wav wma aif aiff ape m4a mpc shn flac', 'mp3', 'ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('mod->mp3', 'alm 669 mdl far xm mod fnk imf it liq mod wow mtm ptm rtm stm s3m ult dmf dbm med okt emod sfx m15 mtn amf gdm stx gmc psm j2b umx amd rad hsc flx gtk mgt mtp', 'mp3', 'xmp -Dlittle-endian -q -c %s', 'lame -r -b %b -S --resample 44.1 - -')");
		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'audio->ogg'");
        
//		update("insert into transcoding2(name, source_formats, target_format, step1) values('flv video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'flv', 'ffmpeg -ss %o -i %s -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");		
//		update("insert into transcoding2(name, source_formats, target_format, step1) values('mkv video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts flv', 'mkv', 'ffmpeg -ss %o -i %s -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
//		update("insert into transcoding2(name, source_formats, target_format, step1) values('webm video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts flv', 'webm', 'ffmpeg -ss %o -t %d -i %s -async 1 -vf lutyuv=y=val*1.3 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f webm -c:v libvpx -preset superfast -c:a libvorbis -threads 0 -')");		
//		update("insert into transcoding2(name, source_formats, target_format, step1) values('wtv video', 'wtv', 'flv', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s 720x360 -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset fast -threads 0 -')");		
//		update("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun/flv video', 'hdrun', 'flv', 'ffmpeg -ss %o -i %z -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");		
//		update("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun/mkv video', 'hdrun', 'mkv', 'ffmpeg -ss %o -i %z -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
//		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv/flv', 'tv', 'flv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -c:v libx264 -preset superfast -threads 0 -')");
//		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv/flv', 'tv', 'mkv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");
//		update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('ogg audio', 'aac wav wma aif aiff ape mp3 m4a mpc shn flac', 'ogg', 'ffmpeg -i %s -map 0:0 -vn -c:a libvorbis -ar 44100 -b:a %bk -v 0 -f ogg -', false)");		
//		update("insert into transcoding2(name, source_formats, target_format, step1) values('mp3 audio', 'ogg oga aac wav wma aif aiff ape m4a mpc shn flac', 'mp3', 'ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -')");		
//		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('mod audio', 'alm 669 mdl far xm mod fnk imf it liq mod wow mtm ptm rtm stm s3m ult dmf dbm med okt emod sfx m15 mtn amf gdm stx gmc psm j2b umx amd rad hsc flx gtk mgt mtp', 'mp3', 'xmp -Dlittle-endian -q -c %s', 'lame -r -b %b -S --resample 44.1 - -')");
//		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('m4a audio', 'm4a', 'mp3', 'ffmpeg -i %s -v 0 -f wav -', 'ffmpeg -i - -acodec libmp3lame -ab 320k -f mp3 -')");
//		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('flac audio', 'flac', 'mp3', 'flac --decode --stdout %s', 'ffmpeg -i - -acodec libmp3lame -ab 320k -f mp3 -')");
//		update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('SubWiji', 'mp3', 'mp3', 'ffmpeg -f mp3 -i %s -ab %bk -v 0 -f mp3 -', false)");
//		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'ogg audio'");
        LOG.info("##Transcoding reseted to Madsonic defaults");
    }    

    public void reset2MadsonicOld() {
        update("delete from transcoding2");
        update("delete from player_transcoding2");
        
        update("insert into transcoding2(name, source_formats, target_format, step1) values('video->flv', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'flv', 'ffmpeg -ss %o -i %s -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset superfast -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('video->mkv', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'mkv', 'ffmpeg -ss %o -i %s -vcodec libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('video->webm', 'avi flv mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts', 'webm', 'ffmpeg -ss %o -t %d -i %s -async 1 -vf lutyuv=y=val*1.3 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f webm -c:v libvpx -preset superfast -c:a libvorbis -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('wtv->flv', 'wtv', 'flv', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s 720x360 -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset fast -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun->flv', 'hdrun', 'flv', 'ffmpeg -ss %o -i %z -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset superfast -threads 0 -')");		
        update("insert into transcoding2(name, source_formats, target_format, step1) values('hdrun->mkv', 'hdrun', 'mkv', 'ffmpeg -ss %o -i %z -vcodec libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv->flv', 'tv', 'flv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -async 1 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f flv -vcodec libx264 -preset superfast -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('tv->mkv', 'tv', 'mkv', 'tail -n1 %s', 'ffmpeg -ss %o -i - -vcodec libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('audio->ogg', 'aac wav wma aif aiff ape mp3 m4a mpc shn flac', 'ogg', 'ffmpeg -i %s -map 0:0 -vn -c:a libvorbis -ar 44100 -b:a %bk -v 0 -f ogg -', false)");		
		update("insert into transcoding2(name, source_formats, target_format, step1) values('audio->mp3', 'ogg oga aac wav wma aif aiff ape m4a mpc shn flac', 'mp3', 'ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('mod->mp3', 'alm 669 mdl far xm mod fnk imf it liq mod wow mtm ptm rtm stm s3m ult dmf dbm med okt emod sfx m15 mtn amf gdm stx gmc psm j2b umx amd rad hsc flx gtk mgt mtp', 'mp3', 'xmp -Dlittle-endian -q -c %s', 'lame -r -b %b -S --resample 44.1 - -')");
		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'audio->ogg'");
        LOG.info("##Transcoding reseted to old Madsonic defaults");
    }     
    
    public void reset2MadsonicWEBM() {
        update("delete from transcoding2");
        update("delete from player_transcoding2");
        update("insert into transcoding2(name, source_formats, target_format, step1) values('all video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts flv', 'webm', 'ffmpeg -ss %o -t %d -i %s -async 1 -vf lutyuv=y=val*1.3 -b %bk -s %wx%h -ar 44100 -ac 2 -v 0 -f webm -vcodec libvpx -preset superfast -acodec libvorbis -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('wtv video', 'wtv', 'webm', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s %wx%h -ar 44100 -ac 2 -v 0 -f webm -vcodec libx264 -preset fast -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('all audio', 'ogg oga aac wav wma aif aiff ape mpc shn', 'mp3', 'ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('mod audio', 'alm 669 mdl far xm mod fnk imf it liq mod wow mtm ptm rtm stm s3m ult dmf dbm med okt emod sfx m15 mtn amf gdm stx gmc psm j2b umx amd rad hsc flx gtk mgt mtp', 'mp3', 'xmp -Dlittle-endian -q -c %s', 'lame -r -b %b -S --resample 44.1 - -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('m4a audio', 'm4a', 'mp3', 'ffmpeg -i %s -v 0 -f wav -', 'ffmpeg -i - -acodec libmp3lame -ab 320k -f mp3 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('flac audio', 'flac', 'mp3', 'flac --decode --stdout %s', 'ffmpeg -i - -acodec libmp3lame -ab 320k -f mp3 -')");
//		update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('SubWiji', 'mp3', 'mp3', 'ffmpeg -f mp3 -i %s -ab %bk -v 0 -f mp3 -', false)");
		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'SubWiji'");
        LOG.info("##Transcoding reseted to WEBM Madsonic defaults");
    }    

    public void reset2MadsonicMP4() {
        update("delete from transcoding2");
        update("delete from player_transcoding2");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('all video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts flv', 'mp4', 'ffmpeg -ss %o -i %s -async 1 -b %bk -s %wx%h -sn -acodec libvorbis -ab 128k -ac 2 -v 0 -vol 256 -f mp4 -vcodec libvpx -quality realtime -cpu-used 5 -threads 0 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1) values('mkv video', 'avi mpg mpeg mp4 m4v mkv mov wmv ogv divx m2ts flv', 'mkv', 'ffmpeg -ss %o -i %s -c:v libx264 -preset superfast -b:v %bk -c:a libvorbis -f matroska -threads 0 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1) values('wtv video', 'wtv', 'mp4', 'ffmpeg -ss %o -i %s -async 30 -b %bk -r 23-.976 -s %wx%h -ar 44100 -ac 2 -v 0 -f mp4 -vcodec libx264 -preset fast -threads 0 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1) values('all audio', 'ogg oga aac wav wma aif aiff ape mpc shn', 'mp3', 'ffmpeg -i %s -map 0:0 -b:a %bk -v 0 -f mp3 -')");		
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('mod audio', 'alm 669 mdl far xm mod fnk imf it liq mod wow mtm ptm rtm stm s3m ult dmf dbm med okt emod sfx m15 mtn amf gdm stx gmc psm j2b umx amd rad hsc flx gtk mgt mtp', 'mp3', 'xmp -Dlittle-endian -q -c %s', 'lame -r -b %b -S --resample 44.1 - -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('m4a audio', 'm4a', 'mp3', 'ffmpeg -i %s -v 0 -f wav -', 'ffmpeg -i - -acodec libmp3lame -ab 320k -f mp3 -')");
		update("insert into transcoding2(name, source_formats, target_format, step1, step2) values('flac audio', 'flac', 'mp3', 'flac --decode --stdout %s', 'ffmpeg -i - -acodec libmp3lame -ab 320k -f mp3 -')");
//		update("insert into transcoding2(name, source_formats, target_format, step1, default_active) values('SubWiji', 'mp3', 'mp3', 'ffmpeg -f mp3 -i %s -ab %bk -v 0 -f mp3 -', false)");
		update("insert into player_transcoding2(player_id, transcoding_id) select distinct p.id, t.id from player p, transcoding2 t where t.name <> 'SubWiji'");
        LOG.info("##Transcoding reseted to MP4 Madsonic defaults");
    }    
    
    /**
     * Updates the given transcoding.
     *
     * @param transcoding The transcoding to update.
     */
    public void updateTranscoding(Transcoding transcoding) {
        String sql = "update transcoding2 set name=?, source_formats=?, target_format=?, step1=?, step2=?, step3=?, default_active=? where id=?";
        update(sql, transcoding.getName(), transcoding.getSourceFormats(),
                transcoding.getTargetFormat(), transcoding.getStep1(), transcoding.getStep2(),
                transcoding.getStep3(), transcoding.isDefaultActive(), transcoding.getId());
    }

    private static class TranscodingRowMapper implements ParameterizedRowMapper<Transcoding> {
        public Transcoding mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new Transcoding(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5),
                    rs.getString(6), rs.getString(7), rs.getBoolean(8));
        }
    }
}
