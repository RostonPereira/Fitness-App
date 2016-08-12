package com.pavan.slidingmenu;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Roston on 23/07/14.
 */
public class updateContact extends Activity {

    String name1=" ",name2=" ",name3=" ",name4=" ",name5=" ";
    String phone1=" ",phone2=" ",phone3=" ", phone4=" ",phone5=" ";
    String email1=" ",email2=" ",email3=" ",email4="",email5=" ";

    EditText text1,text2,text3,text4,text5,text6,text7,text8,text9,text10,text11,text12,text13,text14,text15;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_contact);

        text1=(EditText)findViewById(R.id.contact1name);
        text2=(EditText)findViewById(R.id.contact1email);
        text3=(EditText)findViewById(R.id.contact1phone);


        text4=(EditText)findViewById(R.id.contact2name);
        text5=(EditText)findViewById(R.id.contact2name);
        text6=(EditText)findViewById(R.id.contact2name);


        text7=(EditText)findViewById(R.id.contact3name);
        text8=(EditText)findViewById(R.id.contact3name);
        text9=(EditText)findViewById(R.id.contact3name);


        text10=(EditText)findViewById(R.id.contact4name);
        text11=(EditText)findViewById(R.id.contact4name);
        text12=(EditText)findViewById(R.id.contact4name);


        text13=(EditText)findViewById(R.id.contact5name);
        text14=(EditText)findViewById(R.id.contact5name);
        text15=(EditText)findViewById(R.id.contact5name);


    }


    public void update(View view){

        name1=text1.toString();
        email1=text2.toString();
        phone1=text3.toString();


        name2=text4.toString();
        email2=text5.toString();
        phone2=text6.toString();


        name3=text7.toString();
        email3=text8.toString();
        phone3=text9.toString();

        name4=text10.toString();
        email4=text11.toString();
        phone4=text12.toString();

        name5=text13.toString();
        email5=text14.toString();
        phone5=text15.toString();

        write();

    }


    public void write(){

        SAXBuilder builder=new SAXBuilder();
        AssetManager assets=getAssets();

        try
        {

            InputStream in=assets.open("distresscontact.xml");

            Document document=(Document)builder.build(in);
            Element rootNode=document.getRootElement();

           // List list=rootNode.getChildren("contact");

            Element contact1=rootNode.getChild("contact1");
            contact1.getChild("name").setText(name1);
            contact1.getChild("email").setText(email1);
            contact1.getChild("phonenumber").setText(phone1);


            Element contact2=rootNode.getChild("contact2");
            contact2.getChild("name").setText(name2);
            contact2.getChild("email").setText(email2);
            contact2.getChild("phoneumber").setText(phone2);


            Element contact3=rootNode.getChild("contact3");
            contact3.getChild("name").setText(name3);
            contact3.getChild("email").setText(email3);
            contact3.getChild("phonenumber").setText(phone3);


            Element contact4=rootNode.getChild("contact4");
            contact4.getChild("name").setText(name4);
            contact4.getChild("email").setText(email4);
            contact4.getChild("phonenumber").setText(phone4);


            Element contact5=rootNode.getChild("contact5");
            contact5.getChild("name").setText(name5);
            contact5.getChild("email").setText(email5);
            contact5.getChild("phonenumber").setText(phone5);


            XMLOutputter xmlOutput=new XMLOutputter();
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(document,new FileWriter("distresscontact.xml"));


            System.out.println("File updated!");



        }
        catch (IOException e)
        {
             System.out.println("This is the IO_Exception :" + e);
        }

        catch (JDOMException ae)
        {
            System.out.println("This is the JDOM_Exception :"+ae);
        }

    }


}
