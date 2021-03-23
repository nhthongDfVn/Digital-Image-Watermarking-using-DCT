/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dctwatermarking;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author HoangThong
 */
public class DCTWatermarking extends javax.swing.JFrame {

    /**
     * Creates new form DCTWatermarking
     */
    public static int n = 8,

    /**
     * Creates new form DCTWatermarking
     */
    m = 8;
    public static double pi = 3.142857;
    public int [][][] blocks = new  int[10000][8][8];
    public int[][][] dctBlocks = new int[10000][8][8];
    
    public DCTWatermarking() {
        initComponents();  
    }
    
    public ImageIcon resizePic(String picPath){
        ImageIcon myImg = new ImageIcon(picPath);
        Image img= myImg.getImage().getScaledInstance(jLabelImage.getWidth(), jLabelImage.getHeight(),Image.SCALE_SMOOTH);
        ImageIcon myPicture= new ImageIcon(img);
        return myPicture;
    }
    
    public void devideIntoBlocks(String path) throws IOException{
        File file= new File(path);
        BufferedImage img = ImageIO.read(file);
        //int width          = img.getWidth();
        //int height         = img.getHeight();
        //int dctWidth=width/8;
        //int dctHeight= height/8;
        
        int dctWidth=20;
        int dctHeight= 20;
        
        
        int block=0;
        int startX=0,startY=0;
        int y=0,x=0,i=0,j=0;
        
        
        while (y<=dctHeight){
            while (x<=dctWidth){
                while (j<8){
                    while (i<8){
                        blocks[block][j][i]= new Color(img.getRGB(y+j,x+i), true).getBlue();
                        System.out.println("block: "+block+" j: "+j+" i: "+i+" = "+blocks[block][j][i]+"| value y: "+(y+j)+" , value x: "+(x+i));
                        i++;
                    }
                    i=0;
                    j++;
                }
                j=0;
                // go to next block
                block++;
                if (x+8<=dctWidth-8) {
                    System.out.println("Okay     x = "+(x+8));
                    x=x+8; 
                    }    
                else {
                    x=0;
                    y=y+8;
                }
                if (y+8>dctHeight) return;
            }
        }
    }
    
    
    
    public void createRGBState(String path) throws IOException{
        File file= new File(path);
        BufferedImage img = ImageIO.read(file);
        int width          = img.getWidth();
        int height         = img.getHeight();
        
        int dctWidthBlock=width/8;
        int dctHeightBlock= height/8;
        System.out.println(dctWidthBlock+" "+dctHeightBlock+" "+dctHeightBlock*dctWidthBlock);
        
        for (int y = 0; y < height; y++) {
           for (int x = 0; x < width; x++) {
              //Retrieving contents of a pixel
              int pixel = img.getRGB(x,y);
              //Creating a Color object from pixel value
              Color color = new Color(pixel, true);
              //Retrieving the R G B values
              int red = color.getRed();
              int green = color.getGreen();
              int blue = color.getBlue();
             // System.out.println("Red: "+red+", Green: "+green+", Blue: "+blue+" \n");
           }
        }
    }
    
    public void dctTransform(int block){
        int i, j, k, l;
  
        // dct will store the discrete cosine transform
        
        double ci, cj, dct1, sum;
  
        for (i = 0; i < m; i++) 
        {
            for (j = 0; j < n; j++) 
            {
                // ci and cj depends on frequency as well as
                // number of row and columns of specified matrix
                if (i == 0)
                    ci = 1 / Math.sqrt(m);
                else
                    ci = Math.sqrt(2) / Math.sqrt(m);
                     
                if (j == 0)
                    cj = 1 / Math.sqrt(n);
                else
                    cj = Math.sqrt(2) / Math.sqrt(n);
  
                // sum will temporarily store the sum of 
                // cosine signals
                sum = 0;
                for (k = 0; k < m; k++) 
                {
                    for (l = 0; l < n; l++) 
                    {
                        dct1 = blocks[block][k][l] * 
                               Math.cos((2 * k + 1) * i * pi / (2 * m)) * 
                               Math.cos((2 * l + 1) * j * pi / (2 * n));
                        sum = sum + dct1;
                    }
                }
                dctBlocks[block][i][j] = (int) (ci * cj * sum);
            }
        }
  
        for (i = 0; i < m; i++) 
        {
            for (j = 0; j < n; j++) 
                System.out.printf("%d\t", dctBlocks[block][i][j]);
            System.out.println();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabelImage = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Image");

        jButton1.setText("Choose file");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(52, 52, 52)
                        .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(263, 263, 263)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(250, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(66, 66, 66)
                        .addComponent(jButton1)
                        .addGap(35, 35, 35)
                        .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(98, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        JFileChooser filec= new JFileChooser();
        filec.setCurrentDirectory(new File(System.getProperty("user.home")));
        // check file extension
        
        FileNameExtensionFilter fileFilter= new FileNameExtensionFilter("*.Images","jpg","jpeg","png");
        filec.addChoosableFileFilter(fileFilter);
        
        int fileState= filec.showSaveDialog(null);
        
        // if the user select a file
        
        if (fileState == JFileChooser.APPROVE_OPTION){
            File selectedFile= filec.getSelectedFile();
            String path= selectedFile.getAbsolutePath();
            
            // display the image in the jlanel
            jLabelImage.setIcon(resizePic(path));
            try {
                createRGBState(path);
                devideIntoBlocks(path);
            } catch (IOException ex) {
                Logger.getLogger(DCTWatermarking.class.getName()).log(Level.SEVERE, null, ex);
            }
        }  
        else if (fileState==JFileChooser.ERROR_OPTION){
            System.err.println("Only image are allowed!!!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DCTWatermarking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DCTWatermarking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DCTWatermarking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DCTWatermarking.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DCTWatermarking().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelImage;
    // End of variables declaration//GEN-END:variables
}
