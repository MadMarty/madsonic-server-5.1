<?php

require_once 'HTTP/Request2.php';

if(!function_exists('str_getcsv')) {
	function str_getcsv($input, $delimiter=',', $enclosure='"', $escape=null, $eol=null) {
		$temp = fopen("db.csv", "rw");
		fwrite($temp, $input);
		fseek($temp, 0);
		$r = fgetcsv($temp, 4096, $delimiter, $enclosure);
		fclose($temp);
		return $r;
	}
}

class Music_ArtistInfo {
	
	public $name = '';
	public $musicBrainzId = '';
	public $type = 'Unknown';
	public $begin = null;
	public $end = null;
	public $disambiguation = '';
	public $similar = array();
	
	
	protected $similarDetails = array(); // artists are keys and their similarity rating is the value
	protected $queryName = null; // Given name
	
	/// Config
	protected $adapter = 'curl';
	protected $supportMultipleArtists = true; // support queries like: John Lennon, Paul McCartney and treat
											  // each artist individually, and merge the results.
	
	protected function __construct() {
	}
	
	protected function isCorrectArtist(SimpleXMLElement $xml) {
		$attr = $xml->attributes();
		if($attr === 'Unknown') return false; // Unknown type, ignore
		
		// name similarity
		if(strlen($this->queryName) > 0) {
			$hitNameSimilarity = 0.0;
			similar_text(strtolower($this->queryName), strtolower($artistHit->name), $hitNameSimilarity);
			if($hitNameSimilarity < 20) {
				return false; // name is too different from query name
			}
		}
		
		return true;
	}
	
	protected function getInfoFromXML(SimpleXMLElement $xml) {
		$xml->registerXPathNamespace('mb', 'http://musicbrainz.org/ns/mmd-2.0#');
		$xml->registerXPathNamespace('ext', 'http://musicbrainz.org/ns/ext#-2.0');
		
		// artist
		$xpaths = array(
			//"/mb:metadata/mb:artist-list/mb:artist[@type != 'Unknown']",
			'/mb:metadata/mb:artist[1]',
			'/mb:metadata/mb:artist-list/mb:artist[1]',
		);
		
		
		// Run xpaths to extract artist elements
		foreach($xpaths as $num => $xpath) {
			$artist = $xml->xpath($xpath);
			if($artist === false || count($artist) == 0) {
				if($num === count($xpaths) - 1) {
					throw new Exception('Could not get info from MusicBrainz.');
				}
			}
			else {
				break;
			}
		}
		
		// Choose an artist from the results
		if(count($artist) == 1) {
			$artist = $artist[0];
		}
		else {
			// Iterate from beginning, ignoring Unknown types and hits with names that are <10% similar
			$selectedAHit = false;
			foreach($artist as $artistHit) {
				if($this->isCorrectArtist($artistHit)) {
					$selectedAHit = true;
					$artist = $artist[0];
					break;
				}
			}
			
			if(!$selectedAHit) {
				$artist = $artist[0]; // just take the first one
			}
		}
		
		// id
		$attr = $artist->attributes();
		$this->musicBrainzId = $attr['id'] . '';
		
		// type
		$this->type = $attr['type'] . '';
		
		// name
		$this->name = $artist->name . '';
		
		// disambiguation
		$this->disambiguation = $artist->disambiguation . '';
		
		// begin
		if($artist->{'life-span'}) {
			$tmp = $artist->{'life-span'}->attributes();
			$this->begin = $tmp['begin'] . '';
			if(array_key_exists('end', $tmp)) {
				$this->end = $tmp['end'] . '';
			}
		}
	}
	
	protected function getHTTPRequester() {
		$r = new HTTP_Request2();
		$r->setAdapter($this->adapter);
		return $r;
	}
	
	protected function getArtistInfoFromName($name) {
		$this->queryName = $name;
		
		$req = $this->getHTTPRequester();
		$req->setUrl('http://musicbrainz.org/ws/2/artist/?query='.urlencode($name));
		$req->setMethod(HTTP_Request2::METHOD_GET);
		$req->setConfig(array(
			'follow_redirects' => true
		));
		
		try {
			$xml = simplexml_load_string($req->send()->getBody());
		}
		catch(Exception $e) {
			throw new Exception('Fetching data from MusicBrainz failed: ' . $e->getMessage());
		}
		
		if($xml === false) {
			throw new Exception('Could not get info from Musicbrainz using name. Error: ' . implode("\n", libxml_get_errors()));
		}
		$this->getInfoFromXML($xml);
	}
	
	protected function getArtistInfoFromMusicBrainzId($id) {
		$req = $this->getHTTPRequester();
		$req->setUrl('http://musicbrainz.org/ws/2/artist/'.$id);
		$req->setMethod(HTTP_Request2::METHOD_GET);
		$req->setConfig(array(
			'follow_redirects' => true
		));
		
		try {
			$xml = simplexml_load_string($req->send()->getBody());
		}
		catch(Exception $e) {
			throw new Exception('Fetching data from MusicBrainz using ID failed: ' . $e->getMessage());
		}
		
		if($xml === false) {
			throw new Exception('Could not get info from Musicbrainz. Error: ' . implode("\n", libxml_get_errors()));
		}
		$this->getInfoFromXML($xml);
	}
	
	protected function getSimilarArtists($limit) {
		$this->loadSimilarArtists($this->name, $limit);
	}
	
	protected function loadSimilarArtists($name, $limit) {
		// Last.fm
		$req = $this->getHTTPRequester();
		$req->setUrl('http://ws.audioscrobbler.com/2.0/artist/'.rawurlencode(rawurlencode($name)).'/similar.txt');
		$req->setMethod(HTTP_Request2::METHOD_GET);
		$req->setConfig(array(
			'follow_redirects' => true
		));
		
		try {
			$data = explode("\n", $req->send()->getBody());
		}
		catch(Exception $e) {
			throw new Exception('Requesting info from Last.fm failed: ' . $e->getMessage());
		}
		
		$similar = array();
		for($i = 0, $count = count($data); $i < $count && $i < $limit; $i++) {
			$row = str_getcsv($data[$i]);
			$similar[htmlspecialchars_decode($row[2])] = floatval($row[0]);
		}
		
		$this->similarDetails = $similar;
		$this->similar = array_keys($similar);
		
		return $similar;
	}
	
	public static function createVariousArtistsInfo(array $names, $limit) {
		$artists = array();
				
		// Get info for each one
		foreach($names as $name) {
			$tmpArtist = self::factory($name, $limit);
			$artists[$tmpArtist->name] = $tmpArtist;
		}
		
		// Merge them together in one "super" artist
		$superArtist = new self();
		$superArtist->name = implode(", ", array_keys($artists));
		$superArtist->type = "Various Artists";
		
		
		$similar = array();
		
		foreach($artists as $artist) {
			foreach($artist->similarDetails as $similarArtist => $similarityRating) {
				if(!array_key_exists($similarArtist, $similar)) {
					$similar[$similarArtist] = 0;
				}
				$similar[$similarArtist] += $similarityRating;
			}
		}
		
		arsort($similar);
		
		$superArtist->similar = array_splice(array_keys($similar), 0, $limit);
		
		return $superArtist;
	}
	
	public static function factory($artistOrMBID, $limit = 30) {
		if($limit < 0) { $limit = 30; }
		$a = new self();
		if(preg_match('/^[a-z0-9]+-[a-z0-9]+-[a-z0-9]+-[a-z0-9]+-[a-z0-9]+$/', $artistOrMBID)) {
			// Id
			$a->getArtistInfoFromMusicBrainzId($artistOrMBID);
			
			// similar artists
			$a->getSimilarArtists($limit);
		}
		else {
			
			// Name
			// Need to find ID
			$a->getArtistInfoFromName($artistOrMBID);
			
			$nameSimilarity = 0.0;
			similar_text(strtolower($a->name), strtolower($artistOrMBID), $nameSimilarity);
			$isNotMultiArtists = $nameSimilarity >= 65;//strcasecmp($a->name, $artistOrMBID) === 0;
			
			
			// Check if multiple artists in one artist name (eg. Frank Sinatra & Elivs Presley)
			$names = preg_split("/[\s]*[\/\,\&]+[\s]*/", $artistOrMBID);
			if(!$isNotMultiArtists && count($names) >= 2
			&& !(count($names) == 2 && strcasecmp("the", $names[count($names) - 1]) === 0)) {
				
				return self::createVariousArtistsInfo($names, $limit);
			}
			else {
				// similar artists for this artist
				$a->getSimilarArtists($limit);
			}
		}
		
		return $a;
	}
}