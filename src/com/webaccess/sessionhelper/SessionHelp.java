package com.webaccess.sessionhelper;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

public class SessionHelp {
	
	private SessionHelp(){
		
	}
	/**
	 * Get or create http session
	 *
	 * @return HttpSession
	 */
	public static HttpSession getSession() {
	    return (HttpSession) FacesContext.getCurrentInstance()
	    .getExternalContext().getSession(true);
	}

}


