
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author RK
 */
public class BackLoadImage implements Runnable{

    public List m_All_Products = null;
    public List m_All_Categories = null;
    public BackLoadImage(List m_All_Products, List m_All_Categories){
        this.m_All_Products = m_All_Products;
        this.m_All_Categories = m_All_Categories;
    }
    @Override
    public void run() {
        System.out.println("downloading and saving images...");
        try{
            for(int i = 0; i < m_All_Categories.size(); i++){
                Map product = (Map) m_All_Categories.get(i);
                Object images = (Object)product.get("image");
                String[] image_info = (images.toString()).split(",");
                try{
                    String image_link = image_info[5].replaceFirst("src=", "");
                    if(!saveToLocal(image_link)){
                        System.out.println("saving error in " + image_link);
                    }
                }catch(Exception e){
                    
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        try{
            for(int i = 0; i < m_All_Products.size(); i++){
                Map product = (Map) m_All_Products.get(i);
                ArrayList images = (ArrayList)product.get("images");
                String[] image_info = (images.toString()).split(",");
                try{
                    String image_link = image_info[5].replaceFirst("src=", "");
                    if(!saveToLocal(image_link)){
                        System.out.println("saving error in " + image_link);
                    }
                }catch(Exception e){
                    
                }
            }
        }catch(Exception e){
            System.out.println(e);
        }
        
        System.out.println("downloading and saving images completed");
    }
    
    public boolean saveToLocal(String image_url){
        try{
            URL url = new URL(image_url);
            String[] segments = image_url.split("/");
            String path = "src/main/resources/website";
            File file = new File(path);
            if(!file.exists()) // if direcotory not exist, create it.
                file.mkdirs();
            String absolutePath = file.getAbsolutePath();
            
            File checkFile = new File(absolutePath + "/" + segments[segments.length - 1]);
            System.out.println(absolutePath + "/" + segments[segments.length - 1]);
            if(checkFile.exists()) // if file exist,skip saving.
                return true;
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
               out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            FileOutputStream fos = new FileOutputStream(absolutePath + "/" + segments[segments.length - 1]);
            fos.write(response);
            fos.close();
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
        
        return true;
    }
    
}
