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

 Copyright 2014 (C) Madevil
 */
package org.madsonic.domain;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.madsonic.domain.AccessRight;

/**
 * Represents a Security Group.
 *
 */
public class AccessToken {

    private Integer id;
    private String name;

    private List<AccessRight> accessRights = new LinkedList<AccessRight>(); 
//  private ArrayList<AccessRight> AccessRight = new ArrayList<AccessRight>();

    
    public AccessToken(Integer id, String name, List<AccessRight> accessRight) {
        this.id = id;
        this.name = name;
        this.accessRights = accessRight;
    }

	public AccessToken() {
	}

	public Integer getId() {
        return id;
    }	
	
	public Integer getUserGroupId() {
        return id;
    }

	public void setUserGroupId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}	
	
	public String getUserGroupName() {
		return name;
	}

	public void setUserGroupName(String name) {
		this.name = name;
	}    
	
	public List<AccessRight> getAccessRight() {
		return accessRights;
	}
   
//	@Deprecated
//    public void addAccessRightEX(AccessRight accessRight) {
//		accessRights.add(accessRight);
//    }

    public void addAccessRight(AccessRight accessRight) {
    	accessRights.add(accessRight);
    }    
    
	/**
	 * @return the accessRights
	 */
	public List<AccessRight> getAccessRights() {
		return accessRights;
	}

	/**
	 * @param accessRights the accessRights to set
	 */
	public void setAccessRights(List<AccessRight> accessRights) {
		this.accessRights = accessRights;
	}
	
}