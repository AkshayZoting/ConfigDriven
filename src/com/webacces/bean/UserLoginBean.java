package com.webacces.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.servlet.http.HttpSession;

import com.webacces.model.User;
import com.webacces.util.XMLParser;
import com.webaccess.sessionhelper.SessionHelp;

@SessionScoped
@ManagedBean(name = "userLoginBean")
public class UserLoginBean {

	private User user = new User();
	private String message= "";
	

	public String doLogin() {
		try {
			if(user !=null){
				boolean userExist = XMLParser.validate("C:/data/Users.xml", "/users/user", user);
				if(userExist){
					SessionHelp.getSession().setAttribute("name", user.getName());
					//SessionHelp.getSession().setMaxInactiveInterval(5000);
					return "dataload.xhtml?faces-redirect=true";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		message = "User doesn't exist";
		return "index.xhtml?faces-redirect=true";
	}
	
	public String logout() {
		try{
			HttpSession session = SessionHelp.getSession();
			session.invalidate();			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return "index.xhtml?faces-redirect=true";
	}
	
	public void registerUser(){
		
	}
	
	public String add(){
		
		try {
			if(XMLParser.addUser("C:/data/Users.xml", user)){
				message = "User Created Login to Continue";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "index.xhtml?faces-redirect=true";

	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	

}
