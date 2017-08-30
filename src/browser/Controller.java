/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package browser;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;


/**
 *
 * @author Krishnasis
 */
public class Controller implements Initializable 
{
    String htlink="";
    @FXML
    Button btnGo;
    @FXML 
    TextField addressBar;
    @FXML
    WebView web;
    WebView current;
    @FXML
    Button refresh;
    
    WebEngine engine,currentEngine;
    
    
    Tab currentTab;
    String addressLink="";
    
    @FXML
    TabPane tabs;
    
    @FXML
    Button addNewTab;
    
    String refreshLink="";
    
    @FXML
    Button back, forward;
    public void go(ActionEvent event)
    {
        //current stuff - newly added other than default new tab.
        current = new WebView(); 
        currentEngine = new WebEngine();
        current.setPrefSize(web.getPrefWidth(), web.getPrefHeight()); //set preferences from the first tab
        
        //get the selected tab
        currentTab = tabs.getSelectionModel().getSelectedItem();
        current=(WebView)currentTab.getContent(); //get the webview associated with the tab
        
        currentEngine = current.getEngine();
        
        addressLink = addressBar.getText().toString();
        if(!addressLink.contains("http"))
        {
            addressLink = "http://"+addressLink;
        }
        //load site
        currentEngine.load(addressLink);
        currentEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36");
       
        //if site has finished loading!
        setName();  
        listenToChange(); 
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        currentEngine = web.getEngine();
        currentEngine.load("https://www.google.com");
        currentEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36");
        currentTab = tabs.getSelectionModel().getSelectedItem();       
       
        
        setName();
        
        System.out.println(currentTab.onSelectionChangedProperty().getName());
     

    }
    
    public void listenToChange()
    {
           tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {            
            
                 currentTab = tabs.getSelectionModel().getSelectedItem();
                 current=(WebView)currentTab.getContent(); //get the webview associated with current tab
                 currentEngine = current.getEngine(); 
                 System.out.println(currentEngine.getTitle().toString());
            });       
           currentEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
             {
                if(oldState != newState)
                {
                    System.out.println("haxx");
                }
            }
        });   
           
           
           setName();
    }
    
    public void addTab(ActionEvent event)
    {
        
        Tab tab = new Tab();
        tab.setText("New Tab");
       
        WebView newTab = new WebView();
        WebEngine newTabEngine = newTab.getEngine();
        newTabEngine.load("https://www.google.com");
        tab.setContent(newTab);
        tabs.getTabs().add(tab);  
        
    }
    
    
    public void setTabName(String name)
    {
        currentTab.setText(name);
    }
    @FXML
    public void refresh()
    {
       currentEngine.reload();
       setName();
       
    }
    
    public void setName()
    {
        currentEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                
              String name = currentEngine.titleProperty().toString();
              setTabName(name.substring(name.indexOf("value: ")+"value: ".length(),name.lastIndexOf("-")));
            }
        });   
    }
    
    @FXML
    public void backBtnPressed()
    {
                currentEngine.executeScript("history.back()");
    }
    @FXML
    public void forwardBtnPressed()
    {
        currentEngine.executeScript("history.forward()");
    }
    
     
    
}
