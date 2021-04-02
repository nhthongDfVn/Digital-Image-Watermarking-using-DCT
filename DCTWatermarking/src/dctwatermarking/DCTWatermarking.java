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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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


class Location {
    // values color of image
    public int xVal=0;
    public int yVal=0;
    public int red=0;
    public int green=0;
    public int blue=0;
    
    // dct values
    public int dctVal=-999999; // set giá trị mặc định để kiểm tra xem sử dụng block nào
    public int rDctVal=0;
    
    

    public Location() {
        red=0;
        blue=0;
        green=0;
        xVal=0;
        yVal=0;
        dctVal=-999999;
        rDctVal=0;        
    }

    public void setDctVal(int dctVal) {
        this.dctVal = dctVal;
    }

    public void setrDctVal(int rDctVal) {
        this.rDctVal = rDctVal;
    }

    public int getDctVal() {
        return dctVal;
    }

    public int getrDctVal() {
        return rDctVal;
    }

    public void setxVal(int xVal) {
        this.xVal = xVal;
    }

    public void setyVal(int yVal) {
        this.yVal = yVal;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getxVal() {
        return xVal;
    }

    public int getyVal() {
        return yVal;
    }

    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }
    
    public int getBlue() {
        return blue;
    }
    
    public void setBlue(int blue) {
        this.blue = blue;
    }
    
    
    
    
    
}


public class DCTWatermarking extends javax.swing.JFrame {

    /**
     * Creates new form DCTWatermarking
     */
    public static int n = 8,

    /**
     * Creates new form DCTWatermarking
     */
    m = 8;
    
    
    // ENCODING    
    
    
    public static double pi = 3.142857;
    public Location [][][] blocks = new  Location[10000][8][8];
   // public int[][][] dctBlocks = new int[10000][8][8];
   // public int[][][] inverseBlocks = new int[10000][8][8];
    public Location[] location = new Location[100000];
    public int locaLenght=1;
   
    // DCT alg parameter
    public int coorX,coorY,coorP,coorQ; // vi tri toa do cac diem giau anh
    public int difference;              // do sai lech
    public int t=2;                     // dieu kien do chenh lech d
    public int a=2*(2*t+1);
    public String secretString="011110111011001";      // your bit 010100001010
    
    // image values
    
    public int width;
    public int height;
        
    public int dctWidthBlock=0;
    public int dctHeightBlock=0;
    
    public DCTWatermarking() {
        initComponents();  
       // getImageValue(155);
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
        width          = img.getWidth();
        height         = img.getHeight();
        dctWidthBlock=width/8;
        dctHeightBlock= height/8;
        
      //  int dctWidth=9;
       // int dctHeight= 9;
        
        
        int block=0;
        int startX=0,startY=0;
        int y=0,x=0,i=0,j=0;
        
        System.out.println(dctHeightBlock);
        System.out.println(dctWidthBlock);
        
        while (y<=dctHeightBlock*n){
            while (x<=dctWidthBlock*n){
                while (j<8){
                    while (i<8){
                        blocks[block][j][i]= new Location();
                        // set color of image
                        blocks[block][j][i].setRed(new Color(img.getRGB(y+j,x+i), true).getRed());
                        blocks[block][j][i].setGreen(new Color(img.getRGB(y+j,x+i), true).getGreen());
                        blocks[block][j][i].setBlue(new Color(img.getRGB(y+j,x+i), true).getBlue());
                        // set image coordinates
                        blocks[block][j][i].setxVal(y+j);
                        blocks[block][j][i].setyVal(x+i);
                        
                        
                        
                        //System.out.println("block: "+block+" j: "+j+" i: "+i+" = "+blocks[block][j][i]+"| value y: "+(y+j)+" , value x: "+(x+i));
                        i++;
                        locaLenght++;
                    }
                    i=0;
                    j++;
                }
                j=0;
                // go to next block
                block++;
                if (x+8<=dctWidthBlock*n-8) {
                    x=x+8;
                    //System.out.println("Okay     x = "+x);      
                    }    
                else {
                    x=0;
                    y=y+8;
                }
                if (y+8>dctHeightBlock*n){
                    // for debug
                    return;
                }
            }
        }
        
        
        
        
        
        
    }
    
    
    
    public void createRGBState(String path) throws IOException{
        File file= new File(path);
        BufferedImage img = ImageIO.read(file);
        width          = img.getWidth();
        height         = img.getHeight();
        
        dctWidthBlock=width/8;
        dctHeightBlock= height/8;
        System.out.println(dctWidthBlock+" "+dctHeightBlock+" "+dctHeightBlock*dctWidthBlock);
        
        
        // for testing 
        
        width=9;
        height=9;
        
        
        
        
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
    
    public int getImageValue(int num,int y, int x,int tmp){
        if (y>=n*dctWidthBlock) return tmp;
        if (x>n*dctHeightBlock) return tmp;
        
        
        int block=0;
        int X=0,Y=0;
        /*
        int tempX=num/(dctWidthBlock*n);
        int xnxx=num%(dctWidthBlock*n);
        int tempY=num%(dctHeightBlock*n);
        
        
        if (xnxx!=0&&tempX!=0){
            tempX+=1;
        }
        
        
        X=tempX;
        
        // xac dinh hang
        while (tempX>8){
            block+=dctWidthBlock;
            tempX-=8;
        }
        Y=tempY;
        // xac dinh cot
        while (tempY>=8){
            block+=1;
            tempY-=8;
        }
        
        
        
        X=(X)%8;
        Y=(Y-1)%8;
        */
    /*    block=location[num].getBlock();
        X=location[num].getX();
        Y=location[num].getY();
       //System.out.println("Num: "+num+" Block:"+block+" X: "+X+" Y:"+(Y));
        
        // neu da het secret, khong con blog de luu tru
        if (block>=dctHeightBlock*dctWidthBlock) return tmp;
       // System.out.println("x: "+x+", y: "+y);
        //System.out.println("Block:"+block+" X: "+X+" Y:"+(Y-1));
        
        // kiem tra trong block hoac blocks
        
        if (block>=secretString.length()) return blocks[block][X][Y];
       
        
        return inverseBlocks[block][X][Y];*/
    return 0;
    }
    
    
    
    public void saveImageFile(String path) throws IOException{
        File file= new File(path);
        BufferedImage img = ImageIO.read(file);
        int width          = img.getWidth();
        int height         = img.getHeight();
        
        // new image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        int runVal=1;
        
        for (int y = 0; y < height; y++) {
           for (int x = 0; x < width; x++) {
              //Retrieving contents of a pixel
              int pixel = img.getRGB(x,y);
              //Creating a Color object from pixel value
              Color color = new Color(pixel, true);
              //Retrieving the R G B values
              int red = color.getRed();
              int green = color.getGreen();
              
              int tmp=color.getBlue();
              int blue = getImageValue(runVal,y,x,tmp);
           
             // System.out.println("Red: "+red+", Green: "+green+", Blue: "+blue+" \n");
             runVal++;
              Color color1 = new Color(red,green,blue);
              int rgb = color1.getRGB();
              image.setRGB(x, y, rgb);
             // set color for new image 
           }
        }
        
        

        File outputFile = new File("C:\\Users\\DELL\\Desktop\\output.jpg");
        ImageIO.write(image, "jpg", outputFile);
        
        
        
        
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
                        dct1 = blocks[block][k][l].getBlue() * 
                               Math.cos((2 * k + 1) * i * pi / (2 * m)) * 
                               Math.cos((2 * l + 1) * j * pi / (2 * n));
                        sum = sum + dct1;
                    }
                }
                blocks[block][i][j].setDctVal((int) (ci * cj * sum));

            }
        }
       /* 
        System.out.println("B Blocks");
                        for (i = 0; i < m; i++) 
                        {
                            for (j = 0; j < n; j++) 
                                System.out.printf("%d\t", blocks[block][i][j]);
                            System.out.println();
                        }
        
        System.out.println("DCT Blocks");
        for (i = 0; i < m; i++) 
        {
            for (j = 0; j < n; j++) 
                System.out.printf("%d\t", dctBlocks[block][i][j]);
            System.out.println();
        }
        */
    }
    
    public void dctInverse(int block){
        int i, j, k, l;
  
        // dct inverse transform alg
        
        double ci, cj, dct1, sum;
  
        for (k = 0; k < m; k++) 
        {
            for (l = 0; l < n; l++) 
            {
                sum = 0;
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
                        
                        dct1 = ci * cj*  blocks[block][i][j].getDctVal()* 
                               Math.cos((2 * k + 1) * i * pi / (2 * m)) * 
                               Math.cos((2 * l + 1) * j * pi / (2 * n));
                        sum = sum + dct1;
                    }
                }
                blocks[block][k][l].setrDctVal((int)(sum));
                //inverseBlocks[block][k][l] = (int)(sum);
            }
        }
        
        
     /*   System.out.println("Inverse Blocks");
        for (i = 0; i < m; i++) 
        {
            for (j = 0; j < n; j++) 
                System.out.printf("%d\t", inverseBlocks[block][i][j]);
            System.out.println();
        }*/
        
    }
    
    
    public void convertFile2Binary(String filePath) throws IOException{
         byte[] bytes = Files.readAllBytes(Paths.get(filePath));
    }
    
    public String convertString2Binary(String input){
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))   // char -> int, auto-cast
                            .replaceAll(" ", "0")                         // zero pads
            );
        }
        return result.toString();
    }
    
    
    public void chooseCoefficient(int block){
        
        
        boolean isValid=false; 
        
        // d= || b „(i,j) - b „(p,q) || mod a
        // tính do chenh lenh
        // a=2(2t+1)
        difference=Math.abs(blocks[block][coorX][coorY].getDctVal() -blocks[block][coorP][coorQ].getDctVal())%a;
    }
    
    public boolean isBigger(int num1, int num2){
        return Math.abs(num1)>Math.abs(num2);
    }
    
    public void dctWatermarking(int block, int bitVal){
        // nhung cac bit vao
        // thoa man cac dieu kien
        // d > 2t+1 neu s=1
        // d < 2t+1 neu s=0
        
        
        if ((difference<2*t+1) && (bitVal==1)){
            if (isBigger(blocks[block][coorX][coorY].getDctVal(), blocks[block][coorP][coorQ].getDctVal())){
                blocks[block][coorX][coorY].setDctVal(blocks[block][coorX][coorY].getDctVal()+(int)(0.75*a)-difference); 
            }
            else{
                blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal()-(int)((0.25*a)+difference));
            }
        }  
        
        if ((difference>=2*t+1) && (bitVal==0)){
            if (isBigger(blocks[block][coorX][coorY].getDctVal(), blocks[block][coorP][coorQ].getDctVal())){
                
                blocks[block][coorX][coorY].setDctVal(blocks[block][coorX][coorY].getDctVal()- (difference-(int)(0.75*a)));
                //dctBlocks[block][coorX][coorY]-=(difference-(int)(0.75*a))  ;
            }
            else{
                blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal() + (int)(0.25*a)-difference );
                //dctBlocks[block][coorP][coorQ]+=(int)(0.25*a)-difference   ;
            }
        }
           
    }
    
    public void dctForLoopSecret(){
        int index=0;
        int secLength=secretString.length();
        int secBit=0;
        
        // loop throught secret
        while (index<secLength){
            secBit=Integer.valueOf(secretString.charAt(index));
            dctTransform(index);
            chooseCoefficient(index);
            dctWatermarking(index,secBit);
            index++;
        }
    }
    
    
    
    // DECODE 
    
    
    
    
    

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
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Image");

        jButton1.setText("Choose file");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Digital Image Watermarking using DCT");

        jLabel3.setText("Text");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(121, 121, 121)
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(93, 93, 93)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(280, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(128, 128, 128)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(91, Short.MAX_VALUE))
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
                devideIntoBlocks(path);
                //dctForLoopSecret();
                //saveImageFile(path);
                BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                
                for (int block=0;block<blocks.length;block++){
                    System.out.println("Block: "+block);
                    
                    // check is blocks[block][k][l] is s instanceof Simple1
                    if (!(blocks[block][0][0] instanceof Location)) break;
                    
                    for (int k=0;k<8;k++){
                        for (int l=0;l<8;l++){
                            // setup image val
                            Color color1 = new Color(blocks[block][k][l].getRed(),blocks[block][k][l].getGreen(),blocks[block][k][l].getBlue());
                            int rgb = color1.getRGB();
                            image.setRGB(blocks[block][k][l].getxVal(),blocks[block][k][l].getyVal(), rgb);
                            
                            
                        }
                    }
                }
                

                File outputFile = new File("C:\\Users\\DELL\\Desktop\\output.jpg");
                ImageIO.write(image, "jpg", outputFile);
                
                System.out.println("Done");
                
                
                
                
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
