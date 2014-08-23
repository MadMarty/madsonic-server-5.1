/*
 This file is part of Madsonic.

 Madsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Madsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License		
 along with Madsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2013 (C) Madevil
 */
package org.madsonic.command;

/**
 * Command used in {@link IconSettingsController}.
 *
 * @author Madevil
 */
public class IconCommand {

	private boolean showIconHome; 
	private boolean showIconArtist; 
	private boolean showIconPlaying; 
	private boolean showIconStarred; 
	private boolean showIconRadio; 
	private boolean showIconPodcast; 
	private boolean showIconSettings; 
	private boolean showIconStatus; 
	private boolean showIconSocial; 
	private boolean showIconHistory; 
	private boolean showIconStatistics; 
	private boolean showIconPlaylists; 
	private boolean showIconPlaylistEditor; 
	private boolean showIconMore;
	private boolean showIconAbout;
	private boolean showIconGenre;
	private boolean showIconMoods;
	private boolean showIconCover;

	private boolean showIconAdmins;
	
    private boolean isReloadNeeded;	
    private boolean toast;
	
	/**
	 * @return the showIconHome
	 */
	public boolean isShowIconHome() {
		return showIconHome;
	}
	/**
	 * @param showIconHome the showIconHome to set
	 */
	public void setShowIconHome(boolean showIconHome) {
		this.showIconHome = showIconHome;
	}
	/**
	 * @return the showIconArtist
	 */
	public boolean isShowIconArtist() {
		return showIconArtist;
	}
	/**
	 * @param showIconArtist the showIconArtist to set
	 */
	public void setShowIconArtist(boolean showIconArtist) {
		this.showIconArtist = showIconArtist;
	}
	/**
	 * @return the showIconPlaying
	 */
	public boolean isShowIconPlaying() {
		return showIconPlaying;
	}
	/**
	 * @param showIconPlaying the showIconPlaying to set
	 */
	public void setShowIconPlaying(boolean showIconPlaying) {
		this.showIconPlaying = showIconPlaying;
	}
	/**
	 * @return the showIconStarred
	 */
	public boolean isShowIconStarred() {
		return showIconStarred;
	}
	/**
	 * @param showIconStarred the showIconStarred to set
	 */
	public void setShowIconStarred(boolean showIconStarred) {
		this.showIconStarred = showIconStarred;
	}
	/**
	 * @return the showIconRadio
	 */
	public boolean isShowIconRadio() {
		return showIconRadio;
	}
	/**
	 * @param showIconRadio the showIconRadio to set
	 */
	public void setShowIconRadio(boolean showIconRadio) {
		this.showIconRadio = showIconRadio;
	}
	/**
	 * @return the showIconPodcast
	 */
	public boolean isShowIconPodcast() {
		return showIconPodcast;
	}
	/**
	 * @param showIconPodcast the showIconPodcast to set
	 */
	public void setShowIconPodcast(boolean showIconPodcast) {
		this.showIconPodcast = showIconPodcast;
	}
	/**
	 * @return the showIconSettings
	 */
	public boolean isShowIconSettings() {
		return showIconSettings;
	}
	/**
	 * @param showIconSettings the showIconSettings to set
	 */
	public void setShowIconSettings(boolean showIconSettings) {
		this.showIconSettings = showIconSettings;
	}
	/**
	 * @return the showIconStatus
	 */
	public boolean isShowIconStatus() {
		return showIconStatus;
	}
	/**
	 * @param showIconStatus the showIconStatus to set
	 */
	public void setShowIconStatus(boolean showIconStatus) {
		this.showIconStatus = showIconStatus;
	}
	/**
	 * @return the showIconSocial
	 */
	public boolean isShowIconSocial() {
		return showIconSocial;
	}
	/**
	 * @param showIconSocial the showIconSocial to set
	 */
	public void setShowIconSocial(boolean showIconSocial) {
		this.showIconSocial = showIconSocial;
	}
	/**
	 * @return the showIconStatistics
	 */
	public boolean isShowIconStatistics() {
		return showIconStatistics;
	}
	/**
	 * @param showIconStatistics the showIconStatistics to set
	 */
	public void setShowIconStatistics(boolean showIconStatistics) {
		this.showIconStatistics = showIconStatistics;
	}
	/**
	 * @return the showIconPlaylists
	 */
	public boolean isShowIconPlaylists() {
		return showIconPlaylists;
	}
	/**
	 * @param showIconPlaylists the showIconPlaylists to set
	 */
	public void setShowIconPlaylists(boolean showIconPlaylists) {
		this.showIconPlaylists = showIconPlaylists;
	}
	/**
	 * @return the showIconMore
	 */
	public boolean isShowIconMore() {
		return showIconMore;
	}
	/**
	 * @param showIconMore the showIconMore to set
	 */
	public void setShowIconMore(boolean showIconMore) {
		this.showIconMore = showIconMore;
	}
	/**
	 * @return the showIconAbout
	 */
	public boolean isShowIconAbout() {
		return showIconAbout;
	}
	/**
	 * @param showIconAbout the showIconAbout to set
	 */
	public void setShowIconAbout(boolean showIconAbout) {
		this.showIconAbout = showIconAbout;
	}
	/**
	 * @return the toast
	 */
	public boolean isToast() {
		return toast;
	}
	/**
	 * @param toast the toast to set
	 */
	public void setToast(boolean toast) {
		this.toast = toast;
	}
	/**
	 * @return the isReloadNeeded
	 */
	public boolean isReloadNeeded() {
		return isReloadNeeded;
	}
	/**
	 * @param isReloadNeeded the isReloadNeeded to set
	 */
	public void setReloadNeeded(boolean isReloadNeeded) {
		this.isReloadNeeded = isReloadNeeded;
	}
	/**
	 * @return the showIconGenre
	 */
	public boolean isShowIconGenre() {
		return showIconGenre;
	}
	/**
	 * @param showIconGenre the showIconGenre to set
	 */
	public void setShowIconGenre(boolean showIconGenre) {
		this.showIconGenre = showIconGenre;
	}
	/**
	 * @return the showIconMoods
	 */
	public boolean isShowIconMoods() {
		return showIconMoods;
	}
	/**
	 * @param showIconMoods the showIconMoods to set
	 */
	public void setShowIconMoods(boolean showIconMoods) {
		this.showIconMoods = showIconMoods;
	}
	
	public boolean isShowIconAdmins() {
		return showIconAdmins;
	}
	
	public void setShowIconAdmins(boolean showIconAdmins) {
		this.showIconAdmins = showIconAdmins;
	}
	public boolean isShowIconHistory() {
		return showIconHistory;
	}
	public void setShowIconHistory(boolean showIconHistory) {
		this.showIconHistory = showIconHistory;
	}
	public boolean isShowIconCover() {
		return showIconCover;
	}
	public void setShowIconCover(boolean showIconCover) {
		this.showIconCover = showIconCover;
	}
	/**
	 * @return the showIconPlaylistEditor
	 */
	public boolean isShowIconPlaylistEditor() {
		return showIconPlaylistEditor;
	}
	/**
	 * @param showIconPlaylistEditor the showIconPlaylistEditor to set
	 */
	public void setShowIconPlaylistEditor(boolean showIconPlaylistEditor) {
		this.showIconPlaylistEditor = showIconPlaylistEditor;
	} 
}
