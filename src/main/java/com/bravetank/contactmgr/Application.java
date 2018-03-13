package com.bravetank.contactmgr;

import com.bravetank.contactmgr.model.Contact;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

public class Application {
    //Hold a resuable ref to a SessionFactory (since we need only one)
    private  static final SessionFactory sessionFactory =
            buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        //Create a StandardServiceRegistry
        final ServiceRegistry registry =
                new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        Contact contact = new Contact.ContactBuilder("Teri", "Shelly")
                .withEmail("TeriShelly@yahoo.com")
                .withPhone(7777777777L)
                .build();
        int id = save(contact);

//        //Display a List of Contacts
//        for (Contact c : fetchAllContacts()){
//            System.out.println(c);
//        }
        //Above changed to a Java Stream
        //Display a list of contacts before the update
        System.out.printf("%n%nBefore update%n%n");
        fetchAllContacts().stream().forEach(System.out::println);

        //Get the persisted contact
        Contact c = findContactById(id);

        //Update the contact
        c.setFirstName("Michelle");

        //Persist the changes
        System.out.printf("%n%nUpdating...%n%n");

        update(c);

        System.out.printf("%n%nUpdate complete%n%n");


        //Display a list of contacts after the changes
        System.out.println("After update");
        fetchAllContacts().stream().forEach(System.out::println);

        // Get the contact with id of 1
        c = findContactById(1);

        // Delete the contact
        System.out.printf("%nDeleting...%n");
        delete(c);
        System.out.printf("%nDeleted!%n");
        System.out.printf("%nAfter delete%n");
        fetchAllContacts().stream().forEach(System.out::println);
    }

    private static Contact findContactById(int id){
        //Open a session
        Session session = sessionFactory.openSession();

        //Retrieve the persistent object (or null of not found)
        Contact contact = session.get(Contact.class, id);

        //Close the session
        session.close();

        //Return the object
        return contact;
    }

    //CRUD = Delete
    private static void delete(Contact contact) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Use the session to update the contact
        session.delete(contact);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    //CRUD = Update
    private static void update(Contact contact) {
        //Open a session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction();

        //Use the session to update the contact
        session.update(contact);

        //Commit the transaction
        session.getTransaction().commit();

        //Close the session
        session.close();
    }



    //CRUD = Create
    private static int save(Contact contact) {
        //Open a session
        Session session = sessionFactory.openSession();

        //Begin a transaction
        session.beginTransaction();

        //Use the session to save the contact
        int id = (int) session.save(contact);

        //Commit the transaction
        session.getTransaction().commit();

        //Close the session
        session.close();

        return id;
    }


    //CRUD = Read
    private static List<Contact> fetchAllContacts() {
        // Open a session
        Session session = sessionFactory.openSession();

        // DEPRECATED: Create Criteria
        // Criteria criteria = session.createCriteria(Contact.class);

        // DEPRECATED: Get a list of Contact objects according to the Criteria object
        // List<Contact> contacts = criteria.list();

        // UPDATED: Create CriteriaBuilder
        CriteriaBuilder builder = session.getCriteriaBuilder();

        // UPDATED: Create CriteriaQuery
        CriteriaQuery<Contact> criteria = builder.createQuery(Contact.class);

        // UPDATED: Specify criteria root
        criteria.from(Contact.class);

        // UPDATED: Execute query
        List<Contact> contacts = session.createQuery(criteria).getResultList();

        // Close the session
        session.close();

        return contacts;
    }
}
