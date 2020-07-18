package com.intiformation.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import com.intiformation.DAO.CategorieDAOImpl;
import com.intiformation.DAO.CommandeDAOImpl;
import com.intiformation.DAO.ICategorieDAO;
import com.intiformation.DAO.ICommandeDAO;
import com.intiformation.DAO.ILigneCommandeDAO;
import com.intiformation.DAO.IProduitCategorie;
import com.intiformation.DAO.IProduitDAO;
import com.intiformation.DAO.LigneCommandeDAOImpl;
import com.intiformation.DAO.ProduitCategorieDAOImpl;
import com.intiformation.DAO.ProduitDAOImpl;
import com.intiformation.modeles.Client;
import com.intiformation.modeles.Commande;
import com.intiformation.modeles.LigneCommande;
import com.intiformation.modeles.Produit;
import com.intiformation.modeles.ProduitCategorie;




import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.faces.event.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;


import org.apache.catalina.mapper.MappingData;
import org.primefaces.component.datatable.DataTable;

import org.apache.catalina.manager.util.SessionUtils;


import javax.faces.component.*;
import javax.faces.context.FacesContext;

@ManagedBean(name = "GestionProduitBean")
@SessionScoped
public class GestionProduitBean implements Serializable {

	// _____ Props ______//

	private List<Produit> listeProduits;
	private Produit produit;
	private String motCle;
	private boolean selectionProduit;
	private List<Produit> listePanier = new ArrayList<>();
	private List<Produit>  listeMonde =new ArrayList<>();
	HttpSession session;
	private List<Produit> listeProduitCateg; 
	
	private String nomCategorie;

	private ProduitCategorie produitCateg;

	private List<LigneCommande> listeLigneCommande = new ArrayList<>();
	
	private int nbPersonne = 1;
	private int idClient;

	FacesContext contextJSF = FacesContext.getCurrentInstance();

	// file upload de l'API servlet
	private Part uploadedFile;

	IProduitDAO produitDAO;
	IProduitCategorie produitCategDAO;
	ICommandeDAO commandeDAO;
	ILigneCommandeDAO lignecommandeDAO;
	
	GestionLigneCommandeBean gestionLigneCommandeBean;
	GestionProduitBean gestionProduitBean;
	
	
	Commande commandeRecup;
	
	

	// _____ Ctor ______//

	public GestionProduitBean() {
		produitDAO = new ProduitDAOImpl();
		produitCategDAO = new ProduitCategorieDAOImpl();
		gestionLigneCommandeBean = new GestionLigneCommandeBean();
		commandeDAO = new CommandeDAOImpl();
		lignecommandeDAO = new LigneCommandeDAOImpl();
	}// end ctor vide

	// _____ Méthodes ______//

	/**
	 * Récuperation de la liste des produits dans db via DAO Méthode utilisé par le
	 * composant <h:datatable> de accueil-client.xhtml pour afficher la liste des
	 * livres de la db
	 * 
	 * @return
	 */

	public List<Produit> findAllProduitsBDD() {

		listeProduits = produitDAO.getAll();

		return listeProduits;

	}// end findAllProduitsBDD
	
	

	public List<Produit> findProduitByMotCle(String motCle) {

		System.out.println("Mot clé :" + motCle);

		FacesContext contextJSF = FacesContext.getCurrentInstance();

		listeProduits = produitDAO.getByKeyword(motCle);

		/*
		 * if (motCle != null) {
		 * 
		 * listeProduits = produitDAO.getByKeyword(motCle);
		 * 
		 * if (listeProduits.isEmpty()) {
		 * 
		 * contextJSF.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		 * "Pas de voyage", ": Aucun voyage correspondant à votre recherche...")); }
		 * else {
		 * 
		 */
		return listeProduits;
		/*
		 * }
		 * 
		 * } else {
		 * 
		 * contextJSF.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
		 * "Pas de voyage", ": vous avez pas rentré de requête"));
		 * 
		 * } // end else
		 */

	}// end findProduitByMotClé

	/*
	 * =============================================================================
	 * ===
	 */

	public void selectionnerProduit(ActionEvent event) {

		UIParameter uip = (UIParameter) event.getComponent().findComponent("selectID");
		UIParameter uip2 = (UIParameter) event.getComponent().findComponent("selectIsDispo");

		int idProduit = (int) uip.getValue();
		boolean isDispo = (boolean) uip2.getValue();

		// selection du produit
		Produit produitASelectionner = produitDAO.getById(idProduit);

		// update du selection en true
		isDispo = true;

		produitASelectionner.setSelectionProduit(isDispo);

		produitDAO.update(produitASelectionner);

//		listePanier = produitDAO.getProduitSelectionnes(isDispo);
		
// liste ligne commande		

// A REFAIRE : 
// IF  produitASelectionner already exist in listePanier => fait rien sinon listeLigneCommande.add(ligneCommande);
	
		
		boolean test = listePanier.contains(produitASelectionner);
		
		if (test) {
			System.out.println("if (listePanier.contains(produitASelectionner)): Vrai => NE l'ajoute pas ");


		} else {
			System.out.println("if (listePanier.contains(produitASelectionner)): FAUX => ajoute ");
			listePanier = produitDAO.getProduitSelectionnes(isDispo);
			LigneCommande ligneCommande = new LigneCommande(produitASelectionner.getIdProduit(), 1, produitASelectionner.getPrixProduit());
			listeLigneCommande.add(ligneCommande);
			
		}
		
		
		
//			LigneCommande ligneCommande = new LigneCommande(produitASelectionner.getIdProduit(), 1, produitASelectionner.getPrixProduit());
//			listeLigneCommande.add(ligneCommande);
			
			
			for (LigneCommande ligneCommandeTest : listeLigneCommande) {
				System.out.println("LigneCommande ligneCommandeTest : listeLigneCommande: " + ligneCommandeTest.getProduit_id());
			}
			
			

		FacesContext contextJSF = FacesContext.getCurrentInstance();
		if (session == null) {

			HttpSession session = (HttpSession) contextJSF.getExternalContext().getSession(true);

			session.setAttribute("listePanier", listePanier);
			
			session.setAttribute("listeLigneCommande", listeLigneCommande);

	
		}else {

			HttpSession session = (HttpSession) contextJSF.getExternalContext().getSession(false);

			session.setAttribute("listePanier", listePanier);

			session.setAttribute("listeLigneCommande", listeLigneCommande);
			
		}//end else


	

	}// end selectionnerProduit

	/*
	 * =============================================================================
	 * ===
	 */

	public List<Produit> ListeProduitsSelectionnes() {

		listePanier = produitDAO.getProduitSelectionnes(true);
		
		return listePanier;


	}// end ListeProduitsSelectionnes()

	
	
	
	public List<LigneCommande> ListeLigneCommande() {

		return listeLigneCommande;
	
	}//end ListeLigneCommande()
	
	
	
	

	public double sommePanier() {

		double sommePanier = listePanier.stream().mapToDouble(produit -> produit.getPrixProduit() * getNbPersonne())
				.sum();

		return sommePanier;

	}// end

	/*
	 * =============================================================================
	 * ===
	 */

	/**
	 * Méthode pour supprimer un produit DU PANIER Invoquée au clic sur lien
	 * 'retirer du panier' dans PANIER au clic, l'évenement
	 * 'javax.faces.event.ActionEvent' se déclenche et encapsule toutes les infos
	 * concernant le composant
	 * 
	 * @param event
	 */
	public List<Produit> supprimerProduitduPanier(ActionEvent event) {

		// 1. récup du param passé dans le composant au clic sur le lien 'retirer du
		// panier'

		UIParameter uip = (UIParameter) event.getComponent().findComponent("selectSuppIdPanier");
		UIParameter uip2 = (UIParameter) event.getComponent().findComponent("selectSuppIdLignePanier");
		

		// 2. récup de la valeur du param
		int idProduitSuppDuPanier = (int) uip.getValue();
		int selectSuppIdLignePanier = (int) uip2.getValue();
		
		System.out.println("int selectSuppIdLignePanier =" + selectSuppIdLignePanier);
		

		// 3. récup du panier à retirer du panier
		Produit produitARetirerDuPanier = produitDAO.getById(idProduitSuppDuPanier);

		// update du selection en true
		produitARetirerDuPanier.setSelectionProduit(false);

		// 3.1 récup du context de JSF
		FacesContext contextJSF = FacesContext.getCurrentInstance();

		// 3.2 suppression du livre
		if (produitDAO.update(produitARetirerDuPanier)) {

			contextJSF.addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "Le produit a été supprimé du panier", ""));

		} else {
			contextJSF.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
					" la suppression du produit à échouée", " - le produit n'a pas été supprimé du panier"));

		} // end else
		

		listePanier = produitDAO.getProduitSelectionnes(true);
		
		for (LigneCommande ligneCommande : listeLigneCommande) {
			System.out.println("LigneCommande ligneCommande :" + ligneCommande);
			
		}
		
	
		listeLigneCommande.remove(selectSuppIdLignePanier);
		for (LigneCommande ligneCommande : listeLigneCommande) {
			System.out.println("listeLigneCommande.remove(uipindex):" + ligneCommande);
			
		}
		
		return listePanier;
	}// end supprimerProduit
	
	
	


	/*
	 * =============================================================================
	 * ===
	 */

	/**
	 * Méthode pour supprimer un produit dans la db Invoquée au clic sur lien
	 * 'supprimer' dans admin-accueil au clic, l'évenement
	 * 'javax.faces.event.ActionEvent' se déclenche et encapsule toutes les infos
	 * concernant le composant
	 * 
	 * @param event
	 */
	public void supprimerProduit(ActionEvent event) {

		// 1. récup du param passé dans le composant au clic sur le lien 'supprimer'
		UIParameter uip = (UIParameter) event.getComponent().findComponent("suppID");

		// 2. récup de la valeur du param
		int idProduitSupp = (int) uip.getValue();

		// 3. suppression du produit dans la bdd via id

		// 3.1 récup du context de JSF
		FacesContext contextJSF = FacesContext.getCurrentInstance();

		// 3.2 suppression du livre
		if (produitDAO.delete(idProduitSupp)) {

			contextJSF.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Suppression du produit",
					"- Le produit a été surpprimé avec succès"));

		} else {
			contextJSF.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
					" la suppression du produit à échouée", " - le produit n'a pas été supprimé"));

		} // end else
	}// end supprimerProduit

	/**
	 * permet d'initialiser un produit methode appelée lors de l'ajout du produit
	 *
	 * @param event
	 */
	public void initVoyage(ActionEvent event) {
		setProduit(new Produit());
	}// end initVoyage

	/**
	 * recup d'un produit
	 *
	 * @param event
	 */
	public void recupProduit(ActionEvent event) {

		UIParameter cp = (UIParameter) event.getComponent().findComponent("modifId");
		int id = (int) cp.getValue();

		Produit produit = produitDAO.getById(id);

		setProduit(produit);

	}// end recupProduit

	public void saveVoyage(ActionEvent event) {

		

		// -------------------------------------------
		// cas : ajout
		// -------------------------------------------
		if (produit.getIdProduit() == 0) {

			try {
				// traitement du fileUpload : recup du nom de l'image
				String fileName = uploadedFile.getSubmittedFileName();

				// affectation du nom a la prop urlImage du voyage
				produit.setUrlImageProduit(fileName);
				FacesContext contextJSFajout = FacesContext.getCurrentInstance();	

				// ajout du voyage dans la bdd + message
				if (produitDAO.add(produit)) {
					
					contextJSFajout.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ajout du produit",
							"- Le produit a été ajouté avec succès"));

				} else {
					contextJSFajout.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
							" l'ajout du produit a échoué", " - le produit n'a pas été ajouté"));
				} // end else pour msg ajout

				// ----------------------------------------------
				// ajout de la photo dans le dossier images
				// -----------------------------------------------

				/* ++++++++++++++++++++++++++++++++ version 1 ++++++++++++++++++ */

				// recup du contenu de l'image
				InputStream imageContent = uploadedFile.getInputStream();

				// recup de la valeur du param d'initialisation context-param de web.xml
				FacesContext fContext = FacesContext.getCurrentInstance();
				String pathTmp = fContext.getExternalContext().getInitParameter("file-upload");

				String filePath = fContext.getExternalContext().getRealPath(pathTmp);

				// creation du fichier image (conteneur de l'image)
				File targetFile = new File(filePath, fileName);

				// instanciation du flux de sortie vers le fichier image
				OutputStream outStream = new FileOutputStream(targetFile);
				byte[] buf = new byte[1024];
				int len;

				while ((len = imageContent.read(buf)) > 0) {
					outStream.write(buf, 0, len);
				} // end while

				outStream.close();

			} catch (IOException ex) {
				Logger.getLogger(GestionProduitBean.class.getName()).log(Level.SEVERE, null, ex);
			} // end catch

		} // end if ajout
		
		
		// -------------------------------------------
		// cas : modif
		// -------------------------------------------
		if (produit.getIdProduit() != 0) {

			// ----------------------------------------------
			// creation liaison catégorie - produit
			// -----------------------------------------------

			int IdNewProduit = produit.getIdProduit();
			System.out.println("Id du produit crée :" + IdNewProduit);

			if (uploadedFile != null) {

				String fileNameToUpdate = uploadedFile.getSubmittedFileName();

				if (!"".equals(fileNameToUpdate) && fileNameToUpdate != null) {

					// affectation du nouveau nom à la prop urlImage du voyage
					produit.setUrlImageProduit(fileNameToUpdate);
				} // end if equals
			} // end if uploadedFile != null
			
			FacesContext contextJSFmodif = FacesContext.getCurrentInstance();	

			if (produitDAO.update(produit)) {
				
				contextJSFmodif.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Modification du produit",
						"- Le produit a été modifié avec succès"));

			} else {
				contextJSFmodif.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL,
						" la modification du produit a échouée", " - le produit n'a pas été modifié"));
			} // end else pour msg ajout
			
	
		} // end if modif

	}// end saveBook()

	public void findProduitParCategorie(ActionEvent event){
		
		
		UIParameter cp = (UIParameter) event.getComponent().findComponent("CategorieID");
		int idCategorie = (int) cp.getValue();
		
		UIParameter cpNom = (UIParameter) event.getComponent().findComponent("CategorieNom");
		nomCategorie = (String) cpNom.getValue();
		
		System.out.println("Id Categorie = "  + idCategorie);
		System.out.println("Id Categorie = "  + nomCategorie);
		
		FacesContext contextJSF = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		
		session.setAttribute("nomCategorie", nomCategorie);
		
		listeProduitCateg = produitDAO.getByCategorie(idCategorie);
		
	}//end findProduitParCategorie

	public List<Produit> AfficherlisteProduitMonde(){
		 
		int i = 2;
		
		listeMonde = produitDAO.getByCategorie(i);
		
		return listeMonde; 
	}
	
	public List<Produit> AfficherListeProduitParCateg() {
		
		return listeProduitCateg;
		
	}//end AfficherListeProduit
	
	
	
	
	
	public String ApresPaiement() {
		
		FacesContext contextJSF = FacesContext.getCurrentInstance();
		HttpSession session = (HttpSession) contextJSF.getExternalContext().getSession(false);

		LocalDate date = LocalDate.now();
		Date dateDuJour = Date.valueOf(date);
	
		System.out.println("Date dateDuJour : " + dateDuJour);
		

		Client client = (Client) session.getAttribute("client");
		idClient = client.getId_client();
		
		Commande commande = new Commande(dateDuJour, idClient);

		commandeDAO.add(commande);

		commandeRecup = new Commande();
		commandeRecup = commandeDAO.findIdMax();
		
		int commandeIdRecup = commandeRecup.getId_commande();
		
		
		System.out.println("commandeRecup: " + commandeRecup.getClient_id() + "," 
		+ commandeRecup.getId_commande() + "," + commandeRecup.getDate_commande());
		
		
		for (LigneCommande ligneCommande : listeLigneCommande) {
			
			int produitRecup = ligneCommande.getProduit_id();
			int quantiteRecup = ligneCommande.getQuantite_ligne();
			double PrixRecup = ligneCommande.getPrix_ligne();
			
			LigneCommande ligneCreation = new LigneCommande(commandeIdRecup, produitRecup, quantiteRecup, PrixRecup);

			lignecommandeDAO.add(ligneCreation);
			
		}// end for 
		
		
		listePanier = produitDAO.getProduitSelectionnes(true);

		for (Produit produit : listePanier) {
			produit.setSelectionProduit(false);	
			produitDAO.update(produit);
			System.out.println("produitDAO.update(produit) id: " + produit.getIdProduit() + " : " + produit.isSelectionProduit());
			
		}// end for
		
		
		session.setAttribute("listePanier", null);


		return "validation-commande.xhtml?faces-redirect=true";
		
	}// end ApresPaiement
	
	
	
	
	
	
	
	
	
	
	
	

		
	// _____ Getter /setter ______//
	
	
	public String getNomCategorie() {
		return nomCategorie;
	}

	public void setNomCategorie(String nomCategorie) {
		this.nomCategorie = nomCategorie;
	}
	
	public List<Produit> getListeProduits() {
		return listeProduits;
	}

	public void setListeProduits(List<Produit> listeProduits) {
		this.listeProduits = listeProduits;
	}

	public Produit getProduit() {
		return produit;
	}

	public void setProduit(Produit produit) {
		this.produit = produit;
	}

	public String getMotCle() {
		return motCle;
	}

	public void setMotCle(String motCle) {
		this.motCle = motCle;
	}

	public boolean isSelectionProduit() {
		return selectionProduit;
	}

	public void setSelectionProduit(boolean selectionProduit) {
		this.selectionProduit = selectionProduit;
	}

	public Part getUploadedFile() {
		return uploadedFile;
	}

	public void setUploadedFile(Part uploadedFile) {
		this.uploadedFile = uploadedFile;
	}

	public int getNbPersonne() {
		return nbPersonne;
	}

	public void setNbPersonne(int nbPersonne) {
		this.nbPersonne = nbPersonne;
	}

	public ProduitCategorie getProduitCateg() {
		return produitCateg;
	}

	public void setProduitCateg(ProduitCategorie produitCateg) {
		this.produitCateg = produitCateg;
	}

}// end classe
