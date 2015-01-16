package ng.codehaven.eko.models;

import java.util.ArrayList;

/**
 * Created by Thompson on 09/01/2015.
 * Contact model
 * TODO: Add a photo getter.
 */
public class Contact {
    public String id;
    public String name;
    public String photo_thumbnail_uri;
    public ArrayList<ContactEmail> emails;
    public ArrayList<ContactPhone> numbers;

    public Contact(String id, String name, String photo_thumbnail_uri) {
        this.id = id;
        this.name = name;
        this.photo_thumbnail_uri = photo_thumbnail_uri;
        this.emails = new ArrayList<ContactEmail>();
        this.numbers = new ArrayList<ContactPhone>();
    }

    public void addEmail(String address, String type) {
        emails.add(new ContactEmail(address, type));
    }

    public void addNumber(String number, String type) {
        numbers.add(new ContactPhone(number, type));
    }
}