
import com.icoderman.woocommerce.ApiVersionType;
import com.icoderman.woocommerce.EndpointBaseType;
import com.icoderman.woocommerce.WooCommerce;
import com.icoderman.woocommerce.WooCommerceAPI;
import com.icoderman.woocommerce.oauth.OAuthConfig;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.util.Set;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import org.json.JSONArray;
import org.json.JSONObject;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import org.apache.commons.text.WordUtils;
//import org.apache.catalina.Engine;

public class CustomerPortalFrame extends javax.swing.JFrame {

    //connection string for woocommerce API
    OAuthConfig config = new OAuthConfig("http://alfredosrainelle.com", "ck_cfc8ede8d3bcf409e6b4bc7cae7ada74f48cead1", "cs_37f294f00e6d643a53ae722ef67ab4e03b932feb");
    WooCommerce wooCommerce = new WooCommerceAPI(config, ApiVersionType.V3);
    /////////////////////////////////////////////////////////////////////////////

    ImageIcon icon = new ImageIcon("//Users/businessmac/Desktop/alert-icon-red.png");

    double total = 0;
    double temp_total;
    double tax = 0;
    double total_sum = 0;
    double price = 0;
    double taxPercent = 0.06;

    Dimension old_win_size = null;

//         // Initialize Chromium.
//Engine engine = Engine.newInstance(HARDWARE_ACCELERATED);
    ////////////////////////////////////////////////// run code//////////////////////////////
    // if you press back button, you have to go previous category.
    Stack<Integer> hierachy;

    // varialbel to store all category infos.
    List m_All_categories = null;
    List m_All_products = null;

    static CustomerPortalFrame mainFrame;
    /////////////////////////////////////////////////////////////////////////////////////////////

    ArrayList<Double> prices = new ArrayList<Double>();

    DefaultListModel model;

    // variables for variations and options 
    final JFrame dlg_purchase = new JFrame("Toppings");
    JLabel lb_product_title;
    JLabel lb_total_price = new JLabel();
    private java.util.List<javax.swing.JCheckBox> cb_options = new java.util.ArrayList<>();
    private ButtonGroup rb_variations = new ButtonGroup();
    JLabel lb_variations_title = new JLabel();
    JLabel lb_options_title = new JLabel();
    Map m_options = new HashMap(); // checkbox options
    Map m_variations = new HashMap(); // radio variations
    Map m_purchase_info = new HashMap();
    Map<String, ButtonGroup> m_positions = new HashMap<>();

    // class definitions
    private class MyListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            String label = (String) value;
            String[] arrSplit = label.split("\n");
            String labelText = "<html><h3>";
            for (int i = 0; i < arrSplit.length; i++) {

                if (i == 0) {
                    labelText += arrSplit[i] + "</h3>";
                } else {
                    labelText += arrSplit[i] + "<br/>";
                }
            }
            setText(labelText);

            return this;
        }
    }

    //function to round
    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //function to format numbers
    DecimalFormat df = new DecimalFormat("0.00");

////////////////////////////////////////////////// Run code//////////////////////////////
    // get sub categories from main category
    public static List<Map> getSubCategoriesFromMainCategory(List all_categories, Map main_category) {
        List<Map> results = new ArrayList<>();

        for (int i = 0; i < all_categories.size(); i++) {
            Map category = (Map) all_categories.get(i);
            if ((int) (category.get("parent")) == (int) (main_category.get("id"))) {
                results.add(category);
            }
        }
        return results;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    public void checkBackButton() {
        productPanel.revalidate();
        productPanel.repaint();
        if (hierachy.size() < 1) {
            btn_back.setEnabled(false);
        } else {
            btn_back.setEnabled(true);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    public JLabel createLabelWithImage(String image_link) {
        JLabel lbl_image = null;
        try {
            String[] segments = image_link.split("/");
            String path = "src/main/resources/website";
            File file = new File(path);
            String absolutePath = file.getAbsolutePath();
            File checkFile = new File(absolutePath + "/" + segments[segments.length - 1]);
            System.out.println(absolutePath + "/" + segments[segments.length - 1]);
            URL url = null;
            if (checkFile.exists()) // if file exist, use this image not web.
            {
                url = checkFile.toURL();// getClass().getResource("/website/" + segments[segments.length - 1]);
            } else {
                url = new URL(image_link);
                System.out.println("connecting to " + image_link + "...");
            }
            Image image = ImageIO.read(url);
            Image dImage = image.getScaledInstance(200, 155, Image.SCALE_SMOOTH);//Stores images generated from URLs into Image object dimg
            ImageIcon icon = new ImageIcon(dImage);
            lbl_image = new JLabel(icon);

        } catch (Exception e) {
            try {
                URL url = getClass().getResource("/test/default.jpg");
                Image image = ImageIO.read(url);
                Image dImage = image.getScaledInstance(200, 155, Image.SCALE_SMOOTH);//Stores images generated from URLs into Image object dimg
                lbl_image = new JLabel(new ImageIcon(dImage));
            } catch (IOException ex) {
                Logger.getLogger(CustomerPortalFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lbl_image;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    // show categories
    // m_Main_categories: categories to show
    // m_All_categories: all categories
    // right_panel: boolean to show categories on right panel or not.
    public void showCategories(List<Map> m_Main_categories, List m_All_categories, Map<String, String> param_products, boolean right_panel) {
        // get main category images and show main categories in left panel..
        Border border = LineBorder.createGrayLineBorder();
        if (right_panel) {
            productPanel.removeAll();
            //jPanel1.setLayout(new GridLayout((int)(m_Main_categories.size() / 5) + 1, 5,20,20));
            productPanel.setLayout(new GridLayout((int) (m_Main_categories.size() / 3) + 1, 3, 5, 5));//GridLayout(rows,columns) 
            //jPanel1.setLayout(new GridLayout(20,1)); //GridLayout(rows,columns) //This is right panel
        } else {
            categoryPanel.removeAll();
            categoryPanel.setLayout(new GridLayout(m_Main_categories.size(), 1, 5, 5));
//            categoryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
            //jPanel4.setLayout(new GridLayout(20,1)); //GridLayout(rows,columns) //This is left panel
        }
        for (int i = 0; i < m_Main_categories.size(); i++) {
            Map cur_main_category = m_Main_categories.get(i);
            Object image_info = (Object) cur_main_category.get("image");
            String categoryName = cur_main_category.get("name").toString();

            String str_image_info = image_info.toString();
            String[] array_image_info = str_image_info.split(",");
            String img_link = array_image_info[5].replaceFirst("src=", "");
            int categoryNameLength = categoryName.length();
            System.out.println("Category name = " + categoryName);
            System.out.println("Category name length = " + categoryNameLength);

            if (categoryNameLength >= 20) {
                categoryName = "<html><body>" + categoryName.substring(0, 10) + "<br>" + categoryName.substring(10, 19) + "</body></html>";
            } else if (categoryNameLength < 20 && categoryNameLength > 10) {
                categoryName = "<html><body>" + categoryName.substring(0, 10) + "<br>" + categoryName.substring(10, categoryNameLength) + "</body></html>";
            }
//            JLabel lblItemName = new JLabel(itemName);
            JLabel lbl_image = createLabelWithImage(img_link);
            lbl_image.setAlignmentX(LEFT_ALIGNMENT);
            lbl_image.setHorizontalAlignment(SwingConstants.LEFT);
            lbl_image.setText(categoryName);
            lbl_image.setHorizontalTextPosition(JLabel.CENTER);
            lbl_image.setVerticalTextPosition(JLabel.BOTTOM);
            lbl_image.setBorder(border);
            lbl_image.setFont(new Font(Font.SERIF, Font.BOLD, 25));
//            lbl_image.setSize(new Dimension(20,50));
            // get subcategory from main category
            // if size is 0, show products else show sub categories.
            lbl_image.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent ev) {
                    if (rb_variations.getButtonCount() > 0) {
                        rb_variations.getElements().asIterator().forEachRemaining(entry -> {
                            dlg_purchase.remove(entry);
                            // rb_variations.remove(entry);
                        });
                    }
                    for (int k = 0; k < cb_options.size(); k++) {
                        dlg_purchase.remove(cb_options.get(k));
                    }

                    dlg_purchase.remove(lb_product_title);
                    dlg_purchase.remove(lb_total_price);
                    dlg_purchase.remove(lb_variations_title);
                    dlg_purchase.remove(lb_options_title);

                    Set keys = m_positions.keySet();
                    // To get all key: value
                    for (Object key : keys) {
                        ButtonGroup current_positions = new ButtonGroup();
                        current_positions = m_positions.get(key.toString());
                        if (current_positions.getButtonCount() > 0) {
                            current_positions.getElements().asIterator().forEachRemaining(entry -> {
                                dlg_purchase.remove(entry);
                            });
                        }
                    }

                    if (!right_panel) // if main category is clicked, remove history of hierachy.
                    {
                        hierachy.removeAllElements();
                    }
                    if (ev.getClickCount() == 1 && !ev.isConsumed()) { // check double clicked.
                        productPanel.revalidate();
                        productPanel.repaint();
                        ev.consume();
                        List<Map> subCategories = getSubCategoriesFromMainCategory(m_All_categories, cur_main_category);
                        if (subCategories.isEmpty()) { // thisß category has products no subcategories.
                            hierachy.push((int) cur_main_category.get("id"));
                            mainFrame.setTitle("Loading.......");
                            showProductsOfCategory(param_products, cur_main_category);
                        } else {
                            System.out.println("----------subcategories--------------");
                            hierachy.push((int) cur_main_category.get("id"));
                            mainFrame.setTitle("Loading.......");
                            showCategories(subCategories, m_All_categories, param_products, true);
                        }
                    }
                }
            });
            if (!right_panel) {
                categoryPanel.setAlignmentX(BOTTOM_ALIGNMENT);
                categoryPanel.add(lbl_image);
            } else {
                productPanel.setAlignmentX(BOTTOM_ALIGNMENT);
                productPanel.add(lbl_image);
            }

        }
        checkBackButton();
        this.setTitle("Loading completed");
        System.out.println("loading end");
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    // return products list of category
    public List getProductsOfCategory(int categoryID) {
        List result = new ArrayList();
        for (int i = 0; i < m_All_products.size(); i++) {
            Map cur_product = (Map) m_All_products.get(i);
            ArrayList categories = (ArrayList) cur_product.get("categories");
            for (int p = 0; p < categories.size(); p++) {
                if ((int) ((Map) categories.get(p)).get("id") == categoryID) {
                    result.add(cur_product);
                    break;
                }
            }
        }
        return result;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////

    private void purchaseButtonActionPerform(java.awt.event.ActionEvent evt) {

        String id = m_purchase_info.get("id").toString();
        String name = m_purchase_info.get("name").toString();
        String capturedprice = m_purchase_info.get("capturedprice").toString();
        if (id == null || name == null || capturedprice == null) {
            return;
        }
        double price = Double.parseDouble(capturedprice);

        cartList.setModel(model);
        cartList.setCellRenderer(new MyListCellRenderer());
        String new_purchase =name + "----" + "$" + price;
        //String new_purchase = name + "----" + "$" + price;
        if (m_purchase_info.get("current_variation") != null) {
            new_purchase += "\n" + m_purchase_info.get("current_variation").toString();
        }

        Set keys = m_options.keySet();
        // To get all key: value
        for (Object key : keys) {
            if (m_purchase_info.get(key.toString()) != null) {
                new_purchase += "\n" + m_purchase_info.get(key.toString()).toString();
            }
            if (m_purchase_info.get(key.toString() + "Position") != null) {
                new_purchase += " :  " + m_purchase_info.get(key.toString() + "Position").toString();
            }
        }
        model.addElement(new_purchase);
        total = total + price;
        SubtotalTextBox.setText(df.format(total));
        tax = round(total * taxPercent, 2);
        taxTextBox.setText(df.format(tax));
        total_sum = round(total + tax, 2);
        TotalTextBox.setText(df.format(total_sum));
        prices.add(price);

//              System.out.println(price);
//              System.out.println(m_purchase_info);
    }

    //////////////////////////////////////////////////////////////////////////////
    // create checkboxes for options
    public void createOptions(String label, int base_x, int base_y, String categoryName) {
        lb_options_title = new JLabel(label);
        lb_options_title.setBounds(base_x, base_y + 50, 150, 22);
        dlg_purchase.add(lb_options_title);

        Set keys = m_options.keySet();
        int j = 1;
        for (Object key : keys) {
            javax.swing.JCheckBox option = new javax.swing.JCheckBox(key.toString() + "($" + m_options.get(key) + ")");
            option.setBounds(base_x, base_y + 50 + j * 26, 130, 22);

            // add positions for this topping
            String path = "src/main/resources/icons";
            File file = new File(path);
            String absolutePath = file.getAbsolutePath();
            File f_left = new File(absolutePath + "/" + "pos_left.png");
            File f_right = new File(absolutePath + "/" + "pos_right.png");
            File f_entire = new File(absolutePath + "/" + "pos_entire.png");

            ButtonGroup rb_positions = new ButtonGroup();
            JRadioButton pos_left = new JRadioButton("Left");
            JRadioButton pos_right = new JRadioButton("Right");
            JRadioButton pos_entire = new JRadioButton("Entire");
            // JRadioButton pos_left =  new JRadioButton("Left", new ImageIcon(f_left.getAbsolutePath()));
            // JRadioButton pos_right = new JRadioButton("Right", new ImageIcon(f_right.getAbsolutePath()));
            // JRadioButton pos_entire = new JRadioButton("Entire", new ImageIcon(f_entire.getAbsolutePath()));
            pos_left.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        option.setSelected(true);

                        if (m_purchase_info.get(key.toString() + "Position") == null) {
                            m_purchase_info.put(key.toString() + "Position", pos_left.getText());
                        } else {
                            m_purchase_info.replace(key.toString() + "Position", pos_left.getText());
                        }
                    } else {
                        m_purchase_info.remove(key.toString() + "Position");
                    }
                }
            });
            pos_right.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        option.setSelected(true);

                        if (m_purchase_info.get(key.toString() + "Position") == null) {
                            m_purchase_info.put(key.toString() + "Position", pos_right.getText());
                        } else {
                            m_purchase_info.replace(key.toString() + "Position", pos_right.getText());
                        }
                    } else {
                        m_purchase_info.remove(key.toString() + "Position");
                    }
                }
            });
            pos_entire.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        option.setSelected(true);

                        if (m_purchase_info.get(key.toString() + "Position") == null) {
                            m_purchase_info.put(key.toString() + "Position", pos_entire.getText());
                        } else {
                            m_purchase_info.replace(key.toString() + "Position", pos_entire.getText());
                        }
                    } else {
                        m_purchase_info.remove(key.toString() + "Position");
                    }
                }
            });
            pos_left.setBounds(base_x + 200, base_y + 50 + j * 26, 80, 20);
            pos_right.setBounds(base_x + 280, base_y + 50 + j * 26, 80, 20);
            pos_entire.setBounds(base_x + 360, base_y + 50 + j * 26, 80, 20);

            rb_positions.add(pos_left);
            rb_positions.add(pos_right);
            rb_positions.add(pos_entire);
            m_positions.put(key.toString(), rb_positions);
            if ("Pizza".equals(categoryName)) {
                System.out.println("categoryName");
                dlg_purchase.add(pos_left);
                dlg_purchase.add(pos_right);
                dlg_purchase.add(pos_entire);
            }

            // add event to this checkbox widget
            // option.addActionListener(new ActionListener() {
            //     @Override
            //     public void actionPerformed(ActionEvent e) {
            //         System.out.println(e.getID() == ActionEvent.ACTION_PERFORMED
            //             ? "ACTION_PERFORMED" : e.getID());
            //     }
            // });
            option.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String capturedprice = m_purchase_info.get("capturedprice").toString();
                    double price = Double.parseDouble(capturedprice);
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        price += Double.parseDouble(m_options.get(key).toString());
                        m_purchase_info.replace("capturedprice", price);
                        lb_total_price.setText("Total price:    " + price);

                        // change m_purchase_info with position info
                        String str;
                        str = key.toString() + "($" + m_options.get(key).toString() + ")";

                        if (m_purchase_info.get(key.toString()) == null) {
                            m_purchase_info.put(key.toString(), str);
                        } else {
                            m_purchase_info.replace(key.toString(), str);
                        }
                    } else {
                        price -= Double.parseDouble(m_options.get(key).toString());
                        m_purchase_info.replace("capturedprice", price);
                        lb_total_price.setText("Total price:    " + price);
                        // release selected radiobutton
                        ButtonGroup current_rds = new ButtonGroup();
                        current_rds = m_positions.get(key.toString());
                        current_rds.clearSelection();

                        // remove this option info from m_purhcase_info
                        m_purchase_info.remove(key.toString());
                        m_purchase_info.remove(key.toString() + "Position");
                    }
                }
            });
            cb_options.add(option);
            dlg_purchase.add(option);
            j++;
        }
    }

    // create radio buttons for variations
    public void createVariations(String label, int base_x, int base_y) {

        lb_variations_title = new JLabel(label);
        lb_variations_title.setBounds(base_x, base_y + 40, 100, 22);
        dlg_purchase.add(lb_variations_title);

        rb_variations = new ButtonGroup();

        Set keys = m_variations.keySet();
        int j = 0;
        for (Object key : keys) {
            JRadioButton rb_variation = new JRadioButton();
            if (rb_variations.getSelection() != null) {
                rb_variation = new JRadioButton(key.toString(), false);
            } else {
                rb_variation = new JRadioButton(key.toString(), true);

                // add selected radio button title to m_purchase_info
                String capturedprice = m_purchase_info.get("capturedprice").toString();
                double price = Double.parseDouble(capturedprice);
                m_purchase_info.put("current_variation", key.toString());
                price += Double.parseDouble(m_variations.get(key).toString());
                m_purchase_info.replace("capturedprice", price);
                lb_total_price.setText("Total price: " + price);
            }
            rb_variation.setBounds(base_x + j * 100, base_y + 65, 100, 20);
            // add action listener for this radio button
            rb_variation.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String current_variation = m_purchase_info.get("current_variation").toString();
                    double ex_price = Double.parseDouble(m_variations.get(current_variation).toString());
                    double new_price = Double.parseDouble(m_variations.get(key.toString()).toString());

                    m_purchase_info.replace("current_variation", key.toString());

                    String capturedprice = m_purchase_info.get("capturedprice").toString();
                    double price = Double.parseDouble(capturedprice);
                    price = price - ex_price + new_price;
                    m_purchase_info.replace("capturedprice", price);
                    lb_total_price.setText("Total price: " + price);
                }
            });

            rb_variations.add(rb_variation);
            dlg_purchase.add(rb_variation);
            j++;
        }
    }

    public void createPurchasePanel(String title_variations, String title_options, String categoryName) {

        mainFrame.setEnabled(false);

        int base_x = 20;
        int base_y = 22;
        int bias = 0;
        int width = 500, height = 0;
        String name = m_purchase_info.get("name").toString();
        String capturedprice = m_purchase_info.get("capturedprice").toString();

        lb_product_title = new JLabel("Name:    " + name);
        lb_product_title.setBounds(base_x, base_y, 160, 22);
        dlg_purchase.add(lb_product_title);
        lb_total_price = new JLabel("Total price:    " + capturedprice);
        lb_total_price.setBounds(base_x + 200, base_y, 160, 22);
        dlg_purchase.add(lb_total_price);

        if (title_variations != null) {
            createVariations(title_variations, base_x, base_y);
            bias += 65;
        }

        if (title_options != null) {
            createOptions(title_options, base_x, base_y + bias, categoryName);
            bias += 50 + m_options.size() * 26;
        }

        // add Purchase / Cancel buttons
        JButton btn_purchase = new JButton("Ok");
        btn_purchase.setBounds(width / 2 - 114, base_y + bias + 50, 105, 24);
        JButton btn_cancel = new JButton("Cancel");
        btn_cancel.setBounds(width / 2 + 24, base_y + bias + 50, 105, 24);

        btn_purchase.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                purchaseButtonActionPerform(e);

                //                dlg_purchase.setVisible(false);
                dlg_purchase.remove(btn_purchase);
                dlg_purchase.remove(btn_cancel);
                dlg_purchase.dispose();
                mainFrame.setEnabled(true);
            }
        });
        btn_cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//              dlg_purchase.setVisible(false);
                dlg_purchase.remove(btn_purchase);
                dlg_purchase.remove(btn_cancel);
                dlg_purchase.dispose();
                mainFrame.setEnabled(true);
            }
        });

        dlg_purchase.add(btn_purchase);
        dlg_purchase.add(btn_cancel);

        height = base_y + bias + 120;
        dlg_purchase.setSize(width, height);
        dlg_purchase.setLocationRelativeTo(null);
        dlg_purchase.setLayout(null);
        dlg_purchase.setVisible(true);
        dlg_purchase.addWindowListener(new WindowAdapter() { // anonymous inner class
            @Override
            public void windowClosing(WindowEvent e) {
                mainFrame.setEnabled(true);
                dlg_purchase.remove(btn_purchase);
                dlg_purchase.remove(btn_cancel);

            }

            @Override
            public void windowClosed(WindowEvent e) {
                dlg_purchase.remove(btn_purchase);
                dlg_purchase.remove(btn_cancel);
                dlg_purchase.dispose();
                mainFrame.setEnabled(true);
            }

        });
    }

    // show products according to categories.
    // param_products: woocormerce params.
    public void showProductsOfCategory(Map<String, String> param_products, Map cur_category) {

        // there is no subcategories.
        Border border = LineBorder.createGrayLineBorder();
        int currentCategoryId = (int) cur_category.get("id");
        param_products.put("attributes", Integer.toString(currentCategoryId));
        //List products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), param_products);
        List products = getProductsOfCategory(currentCategoryId);
        productPanel.removeAll();
        //jPanel1.setLayout(new GridLayout((int)(products.size() / 5) + 1, 5,20,20));
        productPanel.setLayout(new GridLayout((int) (products.size() / 3) + 1, 3, 5, 5));//GridLayout(rows,columns) 
//        ProductPanel.setSize(200, 800);
        for (int i = 0; i < products.size(); i++) {
            Map product = (Map) products.get(i);
            ArrayList images = (ArrayList) product.get("images");
            String productName = product.get("name").toString();
            String[] image_info = (images.toString()).split(",");
            String image_link = image_info[5].replaceFirst("src=", "");
            int productNameLength = productName.length();
            try {
                JLabel lbl_ImageProduct = createLabelWithImage(image_link);
                if (productNameLength >= 20) {
                    productName = "<html><body>" + productName.substring(0, 10) + "<br>" + productName.substring(10, 19) + "</body></html>";
                } else if (productNameLength < 20 && productNameLength > 10) {
                    productName = "<html><body>" + productName.substring(0, 10) + "<br>" + productName.substring(10, productNameLength) + "</body></html>";
                }
                lbl_ImageProduct.setText(productName);
                System.out.println("product ko name == " + lbl_ImageProduct.getText());
                lbl_ImageProduct.setHorizontalTextPosition(JLabel.CENTER);
                lbl_ImageProduct.setVerticalTextPosition(JLabel.BOTTOM);
                Font productLabelFont = new Font(Font.SERIF, Font.BOLD, 20);
                lbl_ImageProduct.setFont(productLabelFont);
                lbl_ImageProduct.setBorder(border);
                productPanel.add(lbl_ImageProduct);

                lbl_ImageProduct.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent evpro) {
                        if (evpro.getClickCount() == 1 && !evpro.isConsumed()) {
                            evpro.consume();
                            productPanel.revalidate();
                            productPanel.repaint();
                            String id = Integer.toString((int) product.get("id"));
                            String name = (String) product.get("name");
                            String capturedprice = (String) product.get("price");

                            m_purchase_info.clear();
                            m_purchase_info.put("id", id);
                            m_purchase_info.put("name", name);
                            m_purchase_info.put("capturedprice", capturedprice);

                            // create title and total price for this product
//                                int base_x = -1, base_y = -1;
//                                // find base coodinates for this product's UI
//                                for(int x=0; x < jPanel2.getComponentCount(); x++){
//                                    Component comp=jPanel2.getComponent(x);
//                                    if(comp instanceof Label){
//                                        Label lb_total=(Label) comp;
//                                        if (lb_total.getText() == "SUB-TOTAL") {
//                                            base_x = lb_total.getBounds().x;
//                                            base_y = lb_total.getBounds().y;
//                                            break;
//                                        }
//                                    }
//                                }
                            dlg_purchase.remove(lb_product_title);
                            dlg_purchase.remove(lb_total_price);

                            if (rb_variations.getButtonCount() > 0) {
                                rb_variations.getElements().asIterator().forEachRemaining(entry -> {
                                    dlg_purchase.remove(entry);
                                });
                            }
                            dlg_purchase.remove(lb_variations_title);

                            for (int k = 0; k < cb_options.size(); k++) {
                                dlg_purchase.remove(cb_options.get(k));
                            }
                            dlg_purchase.remove(lb_options_title);

                            Set keys = m_positions.keySet();
                            // To get all key: value
                            for (Object key : keys) {
                                ButtonGroup current_positions = new ButtonGroup();
                                current_positions = m_positions.get(key.toString());
                                if (current_positions.getButtonCount() > 0) {
                                    current_positions.getElements().asIterator().forEachRemaining(entry -> {
                                        dlg_purchase.remove(entry);
                                    });
                                }
                            }

                            // check variations and options for this production
                            // System.out.println(product.get("default_attributes"));
                            // System.out.println(product.get("attributes"));
                            // System.out.println(product.get("variations").toString());
                            // if (product.get("variations").toString() != "[]") {
                            //     List<Map> arr_default_attr = new ArrayList<Map>();
                            //     arr_default_attr = (List)product.get("default_attributes");
                            //     JSONObject jObject= new JSONObject(arr_default_attr.get(0));
                            //     String attr_title = jObject.get("name").toString();
                            //     String default_attr = jObject.get("option").toString();
                            //     List<Map> arr_attrs = new ArrayList<Map>();
                            //     arr_attrs = (List)product.get("attributes");
                            //     jObject= new JSONObject(arr_attrs.get(0));
                            //     JSONArray arr_attributes =  (JSONArray)jObject.get("options");
                            //     if (arr_attributes.length() != 0)
                            //         createVariations(attr_title, default_attr, arr_attributes, base_x, base_y);
                            // }
                            List<Map> meta_data = new ArrayList<Map>();
                            meta_data = (List) product.get("meta_data");

                            m_variations.clear();
                            m_options.clear();
                            m_positions.clear();

                            String title_options = null;
                            String title_variations = null;

                            for (int k = 0; k < meta_data.size(); k++) {
                                String options = meta_data.get(k).get("value").toString();
                                if (options.length() > 222) {

                                    JSONObject jObject_0 = new JSONObject(options);

                                    JSONArray jArr_0 = (JSONArray) jObject_0.get("Rows");
                                    for (int i = 0; i < jArr_0.length(); i++) {
                                        JSONObject jObject = new JSONObject(jArr_0.get(i).toString());
                                        JSONArray jArrary = (JSONArray) jObject.get("Columns");
                                        jObject = new JSONObject(jArrary.get(0).toString());

                                        jObject = new JSONObject(jObject.get("Field").toString());

                                        String col_type = jObject.get("Type").toString();

                                        jArrary = (JSONArray) jObject.get("Options");
                                        if (col_type.equals("checkbox")) {
                                            title_options = jObject.get("Label").toString();
                                            for (int ii = 0; ii < jArrary.length(); ii++) {
                                                JSONObject obj_each = new JSONObject(jArrary.get(ii).toString());

                                                m_options.put(obj_each.get("Label").toString(), obj_each.get("RegularPrice").toString());
                                            }
                                        } else if (col_type.equals("radio")) {
                                            title_variations = jObject.get("Label").toString();
                                            for (int ii = 0; ii < jArrary.length(); ii++) {
                                                JSONObject obj_each = new JSONObject(jArrary.get(ii).toString());

                                                m_variations.put(obj_each.get("Label").toString(), obj_each.get("RegularPrice").toString());
                                            }
                                        }
                                    }

                                    break;
                                }
                            }
                            // create Popup panel
                            String categoryName = cur_category.get("name").toString();
                            createPurchasePanel(title_variations, title_options, categoryName);
                            dlg_purchase.revalidate();
                            dlg_purchase.repaint();

                        }
                    }
                });
            } catch (Exception e) {
                System.out.println(e);
                System.out.println("product display error.");
            }
        }
        checkBackButton();
        mainFrame.setTitle("Loading completed");
        System.out.println("loading end");
    }
///////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Creates new form NewJFrame3
     */
    public CustomerPortalFrame() {
        mainFrame = this;
        this.lb_product_title = new JLabel();
        initComponents();
        categoryScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
//        dlg_purchase = new JDialog(mainFrame, "Toppings", true);
//        dlg_purchase.addWindowListener(new WindowAdapter() { // anonymous inner class
//
//            @Override
//            public void windowClosed(WindowEvent e) {
////                dlg_purchase.remove(btn_purchase);
////                dlg_purchase.remove(btn_cancel);
//                dlg_purchase.dispose();
//                mainFrame.setEnabled(true);
//            }
//
//        });
        dlg_purchase.setAlwaysOnTop(true);//this will keep the popup jframe dlg_purchase always on top and will not lost when clicked on oter area
        model = new DefaultListModel();
        old_win_size = this.getSize();

        ////////////////////////////////////////////////// Run code//////////////////////////////
        hierachy = new Stack<Integer>();

        System.out.println("------------ Run code ----------------------");

        Map<String, String> param_category = new HashMap<>(); //creates parameters Map object
        String max_categories_count = "100"; // 1 to 100
        param_category.put("per_page", max_categories_count);//Sets numbers of result to retrieve
        param_category.put("offset", "0");

        Map<String, String> param_products = new HashMap<>(); //creates parameters Map object
        param_products.put("per_page", "100");//Sets numbers of result to retrieve
        param_products.put("page", "1");

        // fetch all categories
        m_All_categories = wooCommerce.getAll(EndpointBaseType.PRODUCTS_CATEGORIES.getValue(), param_category);
        System.out.println("Categories ==================");
//        System.out.println(m_All_categories);
        //fetch all products
        // m_All_products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), param_products);
        int page = 1;
        while (page > 0) {
            List temp_products = wooCommerce.getAll(EndpointBaseType.PRODUCTS.getValue(), param_products);
            if (temp_products.size() > 0) {
                if (m_All_products == null) {
                    m_All_products = temp_products;
                } else {
                    m_All_products.addAll(temp_products);
                }
                page++;
                param_products.put("page", Integer.toString(page));
            } else {
                page = 0;
            }
        }

        // run thread to download images from website to local reference: /resources/website/....
        BackLoadImage thread_download = new BackLoadImage(m_All_products, m_All_categories);
        new Thread(thread_download).start();

        // fetch main categories
        List<Map> m_Main_categories = new ArrayList<Map>();
        for (int i = 0; i < m_All_categories.size(); i++) {
            Map category = (Map) m_All_categories.get(i);
            if ((int) (category.get("parent")) == 0) {
                m_Main_categories.add(category);
            }
        }

        param_products.put("per_page", "100");//Sets numbers of result to retrieve
        param_products.put("page", "1");
        showCategories(m_Main_categories, m_All_categories, param_products, false);
        System.out.println("-------------------------------------------");
//        System.out.println(param_products);
        ///////////////////////////////////////////////////////////////////////////////////////        
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        orderCartPanel = new javax.swing.JPanel();
        cartList = new javax.swing.JList<>();
        labelSubtotal = new java.awt.Label();
        SubtotalTextBox = new javax.swing.JTextArea();
        labelTotal = new java.awt.Label();
        taxTextBox = new javax.swing.JTextArea();
        taxLabel = new java.awt.Label();
        TotalTextBox = new javax.swing.JTextArea();
        deleteOrderButton = new javax.swing.JButton();
        btn_back = new javax.swing.JButton();
        OrderButton = new javax.swing.JButton();
        containerPanel = new javax.swing.JPanel();
        productScrollPane = new javax.swing.JScrollPane();
        productPanel = new javax.swing.JPanel();
        categoryScrollPane = new javax.swing.JScrollPane();
        categoryPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(1);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        labelSubtotal.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelSubtotal.setText("SUB-TOTAL");

        SubtotalTextBox.setEditable(false);
        SubtotalTextBox.setColumns(20);
        SubtotalTextBox.setRows(5);
        SubtotalTextBox.setText("0");

        labelTotal.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        labelTotal.setText("TOTAL");

        taxTextBox.setEditable(false);
        taxTextBox.setColumns(20);
        taxTextBox.setRows(5);
        taxTextBox.setText("0");

        taxLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        taxLabel.setText("TAX");

        TotalTextBox.setEditable(false);
        TotalTextBox.setColumns(20);
        TotalTextBox.setRows(5);
        TotalTextBox.setText("0");

        deleteOrderButton.setText("Delete Order");
        deleteOrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteOrderButtonActionPerformed(evt);
            }
        });

        btn_back.setText("Back");
        btn_back.setEnabled(false);
        btn_back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_backActionPerformed(evt);
            }
        });

        OrderButton.setText("Order");
        OrderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OrderButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout orderCartPanelLayout = new javax.swing.GroupLayout(orderCartPanel);
        orderCartPanel.setLayout(orderCartPanelLayout);
        orderCartPanelLayout.setHorizontalGroup(
            orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderCartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(orderCartPanelLayout.createSequentialGroup()
                        .addComponent(cartList, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE)
                        .addGap(4, 4, 4))
                    .addGroup(orderCartPanelLayout.createSequentialGroup()
                        .addGroup(orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(deleteOrderButton)
                            .addComponent(labelSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SubtotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(OrderButton, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(taxLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(taxTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labelTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_back, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        orderCartPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {SubtotalTextBox, labelSubtotal});

        orderCartPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {taxLabel, taxTextBox});

        orderCartPanelLayout.setVerticalGroup(
            orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(orderCartPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cartList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(deleteOrderButton, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(OrderButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_back, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(taxLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(labelTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(orderCartPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SubtotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(taxTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TotalTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );

        orderCartPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {SubtotalTextBox, TotalTextBox, labelSubtotal, taxLabel, taxTextBox});

        SubtotalTextBox.getAccessibleContext().setAccessibleParent(SubtotalTextBox);

        containerPanel.setBackground(new java.awt.Color(255, 255, 255));

        productScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Products", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        productPanel.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout productPanelLayout = new javax.swing.GroupLayout(productPanel);
        productPanel.setLayout(productPanelLayout);
        productPanelLayout.setHorizontalGroup(
            productPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 888, Short.MAX_VALUE)
        );
        productPanelLayout.setVerticalGroup(
            productPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1512, Short.MAX_VALUE)
        );

        productScrollPane.setViewportView(productPanel);

        categoryScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Categories"));

        categoryPanel.setBackground(new java.awt.Color(255, 255, 255));
        categoryPanel.setAutoscrolls(true);

        javax.swing.GroupLayout categoryPanelLayout = new javax.swing.GroupLayout(categoryPanel);
        categoryPanel.setLayout(categoryPanelLayout);
        categoryPanelLayout.setHorizontalGroup(
            categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 264, Short.MAX_VALUE)
        );
        categoryPanelLayout.setVerticalGroup(
            categoryPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 750, Short.MAX_VALUE)
        );

        categoryScrollPane.setViewportView(categoryPanel);

        javax.swing.GroupLayout containerPanelLayout = new javax.swing.GroupLayout(containerPanel);
        containerPanel.setLayout(containerPanelLayout);
        containerPanelLayout.setHorizontalGroup(
            containerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(containerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(categoryScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(productScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 435, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        containerPanelLayout.setVerticalGroup(
            containerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(containerPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(containerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(productScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(categoryScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        productScrollPane.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(orderCartPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(orderCartPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(containerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        // TODO add your handling code here:
        System.out.println("resizing....");
        float scaleX = (float) this.getSize().width / (float) this.old_win_size.width;
        float scaleY = (float) this.getSize().height / (float) this.old_win_size.height;

        Component[] main_lbls = categoryPanel.getComponents();
        Component[] product_lbls = productPanel.getComponents();
        for (int i = 0; i < main_lbls.length; i++) {
            JLabel lbl_one = (JLabel) main_lbls[i];
            lbl_one.setPreferredSize(new Dimension((int) ((float) lbl_one.getWidth() * scaleX + 0.5), (int) ((float) lbl_one.getHeight() * scaleY + 0.5)));
        }
        for (int i = 0; i < product_lbls.length; i++) {
            JLabel lbl_one = (JLabel) product_lbls[i];
            lbl_one.setPreferredSize(new Dimension((int) ((float) lbl_one.getWidth() * scaleX + 0.5), (int) ((float) lbl_one.getHeight() * scaleY + 0.5)));
        }
        this.old_win_size = this.getSize();
        productPanel.revalidate();
        productPanel.repaint();
        categoryPanel.revalidate();
        categoryPanel.repaint();
        System.out.println("resizing completed");
    }//GEN-LAST:event_formComponentResized

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        // TODO add your handling code here:
        System.out.println("close test");
    }//GEN-LAST:event_formWindowClosed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        String path = "src/main/resources/website";
        File file = new File(path);
        if (file.exists()) {
            String[] files = file.list();
            for (String s : files) {
                File cur_file = new File(file.getPath(), s);
                cur_file.delete();
            }
        }
    }//GEN-LAST:event_formWindowClosing

    private void OrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OrderButtonActionPerformed
        int totalItem = 0;
        ArrayList<String> orderArray = new ArrayList<String>();
        StringBuilder allOrders = new StringBuilder("");
        ArrayList items = new ArrayList();
        Map<String, Object> order = new HashMap<>();

        for (int i = 0; i < cartList.getModel().getSize(); i++) {
            //  orders = orders + jList2.getModel().getElementAt(i);
            //  allOrders.append("\n").append(jList2.getModel().getElementAt(i));
            orderArray.add(cartList.getModel().getElementAt(i));

            //orders = orders + "\n";
            totalItem = totalItem + 1;

            // System.out.println(jList1.getModel().getElementAt(i));
//           String capturedprice = m_purchase_info.get("capturedprice").toString(); //This needed to be fixed
            String[] capturedprice = cartList.getModel().getElementAt(i).split("\n");//Stores all text of jList1 before new line

            String str = capturedprice[0];
            String Sub_str = str.substring(6); //used to get substrings of a given string

            // extract digits only from strings
            String numberOnly = Sub_str.replaceAll("[^000000000.00000000-999999999.99999999999]", "");//capture just numbers from all text
            //Float Fnum = Float.valueOf(str.substring(0,4));

            // System.out.println(capturedprice);
            String pro_name = cartList.getModel().getElementAt(i);//Will capture All items at the time of purchase in jList1

            String[] pro_info = cartList.getModel().getElementAt(i).split(":::");

            Map<String, Object> Finalcapturedprice = new HashMap<>();

            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("product_id", pro_info[0]);
            productInfo.put("quantity", 1);
            productInfo.put("total", numberOnly);
            productInfo.put("name", pro_name);
            System.out.println("result");
            System.out.println(productInfo);

            items.add(productInfo);
            order.put("line_items", items);

        }
        Map product = wooCommerce.create(EndpointBaseType.ORDERS.getValue(), order);

//             Map<String, Object> params = new HashMap<>();
//         
//            params.put("line_items", items);
//            
        //String capturedprice = m_purchase_info.get("capturedprice").toString();       
        //System.out.println(capturedprice);
        //  orders = allOrders.toString();
        // printer.printString(orderArray, totalItem);
        //
        float subTotal = Float.parseFloat(SubtotalTextBox.getText());
        float tax = Float.parseFloat(taxTextBox.getText());
        float total = Float.parseFloat(TotalTextBox.getText());
//        PrinterJob pj = PrinterJob.getPrinterJob();
//        printing letprint = new printing();
//        pj.setPrintable(letprint,letprint.getPageFormat(pj));
//        try {
//            letprint.printString(orderArray, subTotal, tax, total);
//            pj.print();
//
//        }
//        catch (PrinterException ex) {
//            ex.printStackTrace();
//        }

        //System.out.println(jList2);
        //        countFrequencies(items);
    }//GEN-LAST:event_OrderButtonActionPerformed

    private void btn_backActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_backActionPerformed
        // TODO add your handling code here:
        //        System.out.println(hierachy);
        int category_id = hierachy.pop();
        checkBackButton();
        int parent_sel_category_id = -1;
        List<Map> m_categories = new ArrayList<Map>();
        for (int i = 0; i < m_All_categories.size(); i++) {
            Map category = (Map) m_All_categories.get(i);
            if ((int) category.get("id") == category_id) {
                parent_sel_category_id = (int) category.get("parent");
                break;
            }
        }
        if (parent_sel_category_id == 0) { // parent is main category
            productPanel.removeAll();
            hierachy.removeAllElements();
            mainFrame.setTitle("Loading completed");
            return;
        }

        for (int i = 0; i < m_All_categories.size(); i++) {
            Map category = (Map) m_All_categories.get(i);
            if ((int) category.get("parent") == parent_sel_category_id) {
                m_categories.add(category);
            }
        }
        Map<String, String> param_products = new HashMap<>(); //creates parameters Map object
        param_products.put("per_page", "100");//Sets numbers of result to retrieve
        param_products.put("offset", "0");
        mainFrame.setTitle("Loading......");
        showCategories(m_categories, m_All_categories, param_products, true);
        //        System.out.println("-------------hierachy list-----------");
        //        System.out.println(hierachy);
    }//GEN-LAST:event_btn_backActionPerformed

    private void deleteOrderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteOrderButtonActionPerformed

        if (cartList.getModel().getSize() == 0) {
            JOptionPane.showMessageDialog(this, "No Order to delete!", "Warning", JOptionPane.INFORMATION_MESSAGE, icon);
            return;
        }

        if (cartList.isSelectionEmpty() == true) {
            //JOptionPane.showMessageDialog(null,"Select an Order to delete!","Warning!",JOptionPane.PLAIN_MESSAGE,icon);
            JOptionPane.showConfirmDialog(null, "Select an Order to delete!", "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
            //  JOptionPane.showMessageDialog(this,"Select an Order to delete!");
        }

        int index = cartList.getSelectedIndex();
        double getPrice = prices.get(index);
        DefaultListModel model = (DefaultListModel) cartList.getModel();

        if (index > -1) {
            // JOptionPane.showMessageDialog(this,"Are you sure you want to delete this order?","Warning!",JOptionPane.PLAIN_MESSAGE,icon);
            int selectedOption = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this order?", "Warning!", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, icon);
            if (JOptionPane.YES_OPTION == selectedOption) {
                model.remove(index);
                prices.remove(index);
                //  Subtotal = Subtotal - getPrice;
                total = total - getPrice;
                // update the value in subtotal text box
                SubtotalTextBox.setText(df.format(total));

                tax = tax - getPrice * taxPercent;
                // update the value in tax
                taxTextBox.setText(df.format(tax));

                total_sum = total + tax;
                //update the value in total sum
                TotalTextBox.setText(df.format(total_sum));
            }

        }


    }//GEN-LAST:event_deleteOrderButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OrderButton;
    private javax.swing.JTextArea SubtotalTextBox;
    private javax.swing.JTextArea TotalTextBox;
    private javax.swing.JButton btn_back;
    private javax.swing.JList<String> cartList;
    private javax.swing.JPanel categoryPanel;
    private javax.swing.JScrollPane categoryScrollPane;
    private javax.swing.JPanel containerPanel;
    private javax.swing.JButton deleteOrderButton;
    private javax.swing.JSeparator jSeparator1;
    private java.awt.Label labelSubtotal;
    private java.awt.Label labelTotal;
    private javax.swing.JPanel orderCartPanel;
    private javax.swing.JPanel productPanel;
    private javax.swing.JScrollPane productScrollPane;
    private java.awt.Label taxLabel;
    private javax.swing.JTextArea taxTextBox;
    // End of variables declaration//GEN-END:variables
}
