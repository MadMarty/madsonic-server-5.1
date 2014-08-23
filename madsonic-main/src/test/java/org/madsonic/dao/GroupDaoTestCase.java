package org.madsonic.dao;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.madsonic.domain.AccessGroup;
import org.madsonic.domain.AccessToken;
import org.madsonic.domain.AccessRight;
import org.madsonic.domain.MusicFolder;

import org.madsonic.domain.Group;

public class GroupDaoTestCase extends DaoTestCaseBase {

	protected void setUp() throws Exception {

    	getJdbcTemplate().execute("delete from user_group_access");
//      getJdbcTemplate().execute("delete from music_folder");
//  	getJdbcTemplate().execute("insert into media_folder (user_group_id, music_folder_id, enabled)"); 
    	
//    	getJdbcTemplate().execute("insert into user_group_access (user_group_id, music_folder_id, enabled) " +
//		  						  "(select distinct g.id as user_group_id, f.id as music_folder_id, 'true' as enabled from user_group g, music_folder f)"); }
	
		getJdbcTemplate().execute("insert into user_group_access (user_group_id, music_folder_id, enabled)" + 
				 "(select distinct user_group.id as user_group_id, music_folder.id as music_folder_id, 'true' as enabled from public.user_group user_group, public.music_folder music_folder)"); }

	
    public void testCreateMusicFolder() {
        MusicFolder musicFolder = new MusicFolder(new File("path"), "name", true, new Date(), 1, 1, 0);
        musicFolderDao.createMusicFolder(musicFolder);

        MusicFolder newMusicFolder = musicFolderDao.getAllMusicFolders().get(0);
    }	
    
    public void testGetAllGroups(){
    	List<Group> x = groupDao.getAllGroups();
    }
    
    
//    private AccessToken GetAcessTokenforUser(Group group){
//    	
//    	int UserGroupId = 0;
//    	AccessToken UserToken = accessRightDao.getAccessTokenByGroup(group.getId());
//    	UserToken.setUserGroupId(UserGroupId);
//    	return UserToken; 
//    }    

//    public void testGetAcessTokenforUser(){
//    	AccessToken test = GetAcessTokenforUser(new Group("all"));
//    }
   
    
    public void testCreateAcessGroup(){
    	//Create Group
    	AccessGroup AG = new AccessGroup();

    	//Create Admin Token
    	AccessToken AT_Admin = new AccessToken();
    	AT_Admin.setUserGroupId(0);
    	AT_Admin.addAccessRight(new AccessRight(0,true,true));
    	AT_Admin.addAccessRight(new AccessRight(1,true,true));
    	AT_Admin.addAccessRight(new AccessRight(2,true,true));
    	AT_Admin.addAccessRight(new AccessRight(3,true,false));
    	AT_Admin.addAccessRight(new AccessRight(4,true,false));
    	AG.addAccessToken(AT_Admin);   	

    	//Create Guest Token
    	AccessToken AT_Guest = new AccessToken();
    	AT_Admin.setUserGroupId(1);
    	AT_Guest.addAccessRight(new AccessRight(0,true,true));
    	AT_Guest.addAccessRight(new AccessRight(1,false,true));
    	AT_Guest.addAccessRight(new AccessRight(2,false,true));
    	AT_Guest.addAccessRight(new AccessRight(3,false,false));
    	AT_Guest.addAccessRight(new AccessRight(4,false,false));
    	AG.addAccessToken(AT_Guest);     	
    	
    	assertNotNull(AG);
    }

    public void testCreateAcessToken(){
    	AccessToken AT = new AccessToken();
    	AT.addAccessRight(new AccessRight(0,false,false));
    	AT.addAccessRight(new AccessRight(1,false,false));
    	AT.addAccessRight(new AccessRight(2,false,false));
    	AT.addAccessRight(new AccessRight(3,true,true));
    	AT.addAccessRight(new AccessRight(4,true,true));
    	assertNotNull(AT);
    }       

    public void testCreateAcessRight(){
    	AccessRight AR = new AccessRight();
    	AR.setMusicfolder_id(0);
    	AR.setEnabled(true);
    	assertNotNull(AR);
    	}

}
