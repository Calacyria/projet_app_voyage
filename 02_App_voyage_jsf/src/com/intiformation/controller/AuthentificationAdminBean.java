package com.intiformation.controller;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.intiformation.DAO.AdministrateurDAOImpl;
import com.intiformation.DAO.IAdministrateurDAO;
import com.intiformation.modeles.Administrateur;

/**
 * MB pour la gestion de l'authentification de l'administrateur 
 * 
 * @author vincent
 *
 */

@ManagedBean(name="AuthentificationAdminBean")
@SessionScoped
public class AuthentificationAdminBean implements Serializable {
	
//  ----- Props ----
	private String adminIdentifiant;
	private String adminMotDePasse;
	
	private Administrateur administrateur;
	
	// déclaration de la DAO
	private IAdministrateurDAO administrateurDAO;

	
	//  ----- Ctors ---- ctor vide pour l'instanciation du MB 
	public AuthentificationAdminBean() {
		administrateurDAO = new AdministrateurDAOImpl();
	}// end ctor 
	
	
	
//  ----- Meths ----
	/**
	 * meth qui permet de faire connecter l'admin à son espace et lui créer une session http
	 * la meth sera invoquée à la soumission du formulaire d'authentification admin
	 * 
	 * @return la page d'accueil-admin
	 */
	public String connecterAdministrateur() {
		
		// 1. déclaration du context de JSF (l'objet FacesContext)
		/**
		 * javax.faces.FacesContext : encapsule l'arbre des composants coté serveur et la rqt/rep http
		 */
		FacesContext contextJSF = FacesContext.getCurrentInstance();
		
		// 2.vérif si admin existe dans la bdd 
		if (administrateurDAO.isAdministrateurExists(adminIdentifiant, adminMotDePasse)) {
			
			// si vrai => admin existe dans bdd
			// -> création de la session
			HttpSession session = (HttpSession) contextJSF.getExternalContext().getSession(true);
			
			// -> sauvegarde du login de l'utilisateur dans la session
			session.setAttribute("user_login", adminIdentifiant);
			
			// -> navigation vers la page "accueil-admin.xhtml"
			return "accueil-admin.xhtml?faces-redirect=true";
			
			
		}else {
			// l'admin n'existe pas la bdd
			/**
			 * envoi d'un msg vers la vue via l'objet 'FacesMassage'
			 * et l'objet 'FacesContext'
			 */
			
			// definition du msg à envoyer via un objet de type 'FacesMassage'
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Echec de connexion :", " Identifiant ou MdP invalide ");
			
			// envoi du msg vers la vue via le context de JSF (l'objet 'FacesContext') et sa methode addMessage
			contextJSF.addMessage(null, message);
			
			// -> navigation vers la page du formulaire "authentification.xhtml"
						return "authentification.xhtml?faces-redirect=true";
			
			
		}// end else
	}// end connecterAdministrateur
	
	
	
	/**
	 * meth qui permet de faire deconnecter l'administrateur de son espace et de détruire la session http
	 * la meth sera invoquée au click sur se deconnecter
	 * 
	 * @return page d'accueil-client du site
	 */
	public String deconnecterAdministrateur() {
		
		// 1. récup du contexte de JSF 
		FacesContext contextJSF = FacesContext.getCurrentInstance();
		
		// 2. récup de la session http de l'admin 
		HttpSession session = (HttpSession) contextJSF.getExternalContext().getSession(false);
		
		// 3. deconnexion
		session.invalidate();
		
		// 4. message de deconnexion vers la vue 
		FacesMessage messageDeconnexion = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deconnexion", " - vous êtes maintenant déconnecté");
		contextJSF.addMessage(null, messageDeconnexion);
		
		// 5. redirection vers la page du formulaire
		return "accueil-client.xhtml?faces-redirect=true";
				
		
	}// end deconnecterAdministrateur()



	
	//  ----- Getters/setters ----
	public String getAdminIdentifiant() {
		return adminIdentifiant;
	}



	public void setAdminIdentifiant(String adminIdentifiant) {
		this.adminIdentifiant = adminIdentifiant;
	}



	public String getAdminMotDePasse() {
		return adminMotDePasse;
	}



	public void setAdminMotDePasse(String adminMotDePasse) {
		this.adminMotDePasse = adminMotDePasse;
	}



	public Administrateur getAdministrateur() {
		return administrateur;
	}



	public void setAdministrateur(Administrateur administrateur) {
		this.administrateur = administrateur;
	}


}// end class AuthentificationAdminBean