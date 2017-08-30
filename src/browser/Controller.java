/*
 * A simple JavaFX browser!
*/
package browser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
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
/*Go Button is pressed*/
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
    Button back, forward, viewSource;
    public void go(ActionEvent event) throws IOException
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
           
            
            if(checkUrlExists("http://"+addressLink))
            {   
                    addressLink = "http://"+addressLink;
            }
            else
            {
                    addressLink = "https://www.google.co.in/search?q="+addressLink;
            }
        }

        //load site
        currentEngine.load(addressLink);
        currentEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36");
       
        //if site has finished loading!
        addressBar.setText(currentEngine.getLocation().toString());
        setName();  
        listenToChange(); 
    }
    /*Start of browser*/
    @Override
    public void initialize(URL location, ResourceBundle resources) 
    {
        currentEngine = web.getEngine();
        currentEngine.load("https://www.google.com");
        currentEngine.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36");
        currentTab = tabs.getSelectionModel().getSelectedItem();       
       
        
        setName();
        
        System.out.println(currentTab.onSelectionChangedProperty().getName());
        updateAdBar(currentEngine.getLocation().toString());

     

    }
    /*Listen to changes in activity*/
    public void listenToChange()
    {
           tabs.getSelectionModel().selectedItemProperty().addListener((ov, oldTab, newTab) -> {            
            
                 currentTab = tabs.getSelectionModel().getSelectedItem();
                 current=(WebView)currentTab.getContent(); //get the webview associated with current tab
                 currentEngine = current.getEngine(); 
                 System.out.println(currentEngine.getTitle().toString());
                 setTabName(currentEngine.getTitle().toString());
                 updateAdBar(currentEngine.getLocation().toString());
                 
            });       
           currentEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
             {
                if(oldState != newState)
                {
                    System.out.println("haxx");
                     updateAdBar(currentEngine.getLocation().toString());

                }
               
            }
        });   
                 
           
           setName();
    }
    /*Add Tab Button*/
    public void addTab(ActionEvent event)
    {
        
        Tab tab = new Tab();
        tab.setText("New Tab");
       
        WebView newTab = new WebView();
        WebEngine newTabEngine = newTab.getEngine();
        newTabEngine.load("https://www.google.com");
        tab.setContent(newTab);
        tabs.getTabs().add(tab);  
        updateAdBar(currentEngine.getLocation().toString());        
    }
    /*Add new Tab for source viewing : Default is Google for now*/
    public void addTab(String code)
    {
        
        Tab tab = new Tab();
        tab.setText("Source Code");
       
        WebView newTab = new WebView();
        WebEngine newTabEngine = newTab.getEngine();
        newTabEngine.loadContent(code,"text/plain");
        tab.setContent(newTab);
        tabs.getTabs().add(tab);  
        updateAdBar(currentEngine.getLocation().toString());        
    }
    
    /*Set Tab Name*/
    public void setTabName(String name)
    {
        currentTab.setText(name);
        updateAdBar(currentEngine.getLocation().toString());

    }
    /*Refresh Page*/
    @FXML
    public void refresh()
    {
       currentEngine.reload();
       setName();
      updateAdBar(currentEngine.getLocation().toString());

       
    }
    /*Set Name of Tab*/
    public void setName()
    {
        currentEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                
              String name = currentEngine.titleProperty().toString();
              setTabName(name.substring(name.indexOf("value: ")+"value: ".length(),name.lastIndexOf("-")));
            }
        });   
    }
    
    /*back button is pressed*/
    @FXML
    public void backBtnPressed()
    {
                currentEngine.executeScript("history.back()");
                updateAdBar(currentEngine.getLocation().toString());
    }
    /*Forward button is pressed*/
    @FXML
    public void forwardBtnPressed()
    {
        currentEngine.executeScript("history.forward()");
        updateAdBar(currentEngine.getLocation().toString());
    }
    /*Update the address bar*/
    public void updateAdBar(String address)
    {
             addressBar.setText(address);
    }
    /*View Source of website*/
    @FXML
    public void viewSource()throws IOException
    {
        
        if(tabs.getTabs().size() >= 1)
        {
            String urlToView = currentEngine.getLocation();
            URL url = new URL(urlToView);
            InputStreamReader ir = new InputStreamReader(url.openStream());
            BufferedReader br = new BufferedReader(ir);
            String source = "";
            String code="";
            while((source=br.readLine())!=null)
            {
                code=code+source+"\n\n";
            }
            addTab(code);

            br.close();
            ir.close();   
        }
    }
    /*Check if URL exists */
    public boolean checkUrlExists(String url)throws IOException
        {
               URL urlToCheck = new URL(url);
               HttpURLConnection huc = (HttpURLConnection) urlToCheck.openConnection();
               huc.setRequestMethod("GET");
               int code;
               try
               {
                   huc.connect();
                   code =huc.getResponseCode();
               }
               catch(Exception e) //url does not exist
               {
                   code = -1232; //please don't judge
               }
            
              if(code==-1232)
                  return false;
              return true;
        }
}
