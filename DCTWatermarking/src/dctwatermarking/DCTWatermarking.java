/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dctwatermarking;

import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
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
    public Location [][][] blocks = new  Location[100000][8][8];
    public int locaLenght=1;
   
    // DCT alg parameter
    public int coorX,coorY,coorP,coorQ; // vi tri toa do cac diem giau anh
    public int difference;              // do sai lech
    public int t=2;                     // dieu kien do chenh lech d
    public int a=2*(2*t+1);
    public String secretString="";      // your bit 010100001010
    public int fileLength=24;          // chen vao do dai cua file
    
    
    // image values
    
    public int width;
    public int height;
        
    public int dctWidthBlock=0;
    public int dctHeightBlock=0;
    
    // file srce
    public String srcOriginalFile="";
    public String srcEmbedFile="";
    
    public DCTWatermarking() {
        initComponents();  
    }
    
    public ImageIcon resizePic(String picPath){
        ImageIcon myImg = new ImageIcon(picPath);
        Image img= myImg.getImage().getScaledInstance(jLabelImage2.getWidth(), jLabelImage2.getHeight(),Image.SCALE_SMOOTH);
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
        
        
        int block=0;
        int startX=0,startY=0;
        int y=0,x=0,i=0,j=0;
        
        
        while (y<=dctHeightBlock*n){
            while (x<=dctWidthBlock*n){
                while (j<8){
                    while (i<8){
                        blocks[block][j][i]= new Location();
                        // set color of image
                        blocks[block][j][i].setRed(new Color(img.getRGB(x+i,y+j), true).getRed());
                        blocks[block][j][i].setGreen(new Color(img.getRGB(x+i,y+j), true).getGreen());
                        blocks[block][j][i].setBlue(new Color(img.getRGB(x+i,y+j), true).getBlue());
                        // set image coordinates
                        blocks[block][j][i].setxVal(y+j);
                        blocks[block][j][i].setyVal(x+i);
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
                    }    
                else {
                    x=0;
                    y=y+8;
                }
                if (y+8>dctHeightBlock*n){
                    // for debug
                    System.out.println("[INFO] Read file okay");
                    return;
                }
            }
        }   
    }
    
    
    public int getImageValue(int block, int x, int y){
        int tmp;
        if (blocks[block][x][y].getDctVal()!=-999999){
            tmp=((blocks[block][x][y].getrDctVal()>255) ? 255 : blocks[block][x][y].getrDctVal()); 
            tmp=(tmp<0)?0:tmp;
            return tmp;
        }
        else{
            return blocks[block][x][y].getBlue();
        }
     
    }
    
    public boolean  canWatermarking() throws IOException{
        // Original Image
        File file= new File(srcOriginalFile);
        BufferedImage img = ImageIO.read(file);
        width          = img.getWidth();
        height         = img.getHeight();
        
        dctWidthBlock=width/8;
        dctHeightBlock= height/8;
        
        int lenght1=dctHeightBlock*dctWidthBlock;
        
        // Embed Image
        int lenght2=convertFile2Binary(srcEmbedFile);
        
        if (lenght1>=lenght2+fileLength){
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    public BufferedImage readRestData(BufferedImage image) throws IOException{
        // chieu dung
        File file= new File(srcOriginalFile);
        BufferedImage img = ImageIO.read(file);
        
        for (int i=1;i<height;i++){
            for (int j=dctWidthBlock*8;j<width;j++){
               // System.out.println(j+" "+i);
                 int pixel = img.getRGB(j,i);
                 image.setRGB(j,i, pixel);   
            }
        }
        
        for (int i=dctHeightBlock*8;i<height;i++){
            for (int j=1;j<width;j++){
               // System.out.println(j+" "+i);
                 int pixel = img.getRGB(j,i);
                 image.setRGB(j,i, pixel);   
            }
        }
        return image;
    }
    
    public void saveImageFile() throws IOException{
        int ll=0;
      BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                for (int block=0;block<blocks.length;block++){
                    // check is blocks[block][k][l] is s instanceof Simple1
                    if (!(blocks[block][0][0] instanceof Location)) break;
                    
                    for (int k=0;k<8;k++){
                        for (int l=0;l<8;l++){
                            // setup image val
                            //ll=getImageValue(block,k,l);
                            //System.out.println(ll);
                            Color color1 = new Color(blocks[block][k][l].getRed(),blocks[block][k][l].getGreen(),getImageValue(block,k,l));
                            int rgb = color1.getRGB();
                            image.setRGB(blocks[block][k][l].getyVal(),blocks[block][k][l].getxVal(), rgb); 
                        }
                    }
                }
                
                image=readRestData(image);
                File outputFile = new File("C:\\Users\\DELL\\Desktop\\output1.jpg");
                ImageIO.write(image, "jpg", outputFile);
                
                System.out.println("[INFO] Done");
        
        
        
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
                //System.out.println(blocks[block][k][l].getBlue()+" "+blocks[block][k][l].getrDctVal());
                //inverseBlocks[block][k][l] = (int)(sum);
            }
        }
    }
    
    
    public int convertFile2Binary(String filePath) throws IOException{
        File file= new File(filePath);
        BufferedImage image = ImageIO.read(file);
        
       // write it to byte array in-memory (jpg format)
       ByteArrayOutputStream b = new ByteArrayOutputStream();
       ImageIO.write(image, "png", b);

       // do whatever with the array...
       byte[] jpgByteArray = b.toByteArray();

       // convert it to a String with 0s and 1s        
       StringBuilder sb = new StringBuilder();
       for (byte by : jpgByteArray)
           sb.append(Integer.toBinaryString(by & 0xFF));

       //System.out.println();
       secretString=sb.toString();
       //System.out.println(secretString.length());
       return secretString.length();
       
         
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
                //System.out.println("TH1: old: "+blocks[block][coorX][coorY].getGreen()+" new: "+blocks[block][coorX][coorY].getDctVal());
            }
            else{
                blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal()-(int)((0.25*a)+difference));
               // System.out.println("TH2: old: "+blocks[block][coorP][coorQ].getGreen()+" new: "+blocks[block][coorP][coorQ].getDctVal());
            }
        }  
        
        if ((difference>=2*t+1) && (bitVal==0)){
            if (isBigger(blocks[block][coorX][coorY].getDctVal(), blocks[block][coorP][coorQ].getDctVal())){
                
                blocks[block][coorX][coorY].setDctVal(blocks[block][coorX][coorY].getDctVal()- (difference-(int)(0.75*a)));
                //System.out.println("TH3: old: "+blocks[block][coorX][coorY].getGreen()+" new: "+blocks[block][coorX][coorY].getDctVal());
                //dctBlocks[block][coorX][coorY]-=(difference-(int)(0.75*a))  ;
            }
            else{
                blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal() + (int)(0.25*a)-difference );
               // System.out.println("TH4: old: "+blocks[block][coorP][coorQ].getGreen()+" new: "+blocks[block][coorP][coorQ].getDctVal());
                //dctBlocks[block][coorP][coorQ]+=(int)(0.25*a)-difference   ;
            }
        }
        
       
           
    }
    
    public void dctForLoopSecret(){
        int index=0;
        int secLength=secretString.length();
        int secBit=0;
        
        String tmp=Integer.toBinaryString(secLength);
        for (int i=tmp.length();i<fileLength;i++){
            tmp="0"+tmp;
        }
        
        // loop throught secret
        
        secretString=tmp +secretString; // thêm độ dài của file vào
        secLength=secretString.length();
        
        
        while (index<secLength){
            secBit=secretString.charAt(index)-48;
           //System.out.println(secBit);
            dctTransform(index);
            chooseCoefficient(index);
            dctWatermarking(index,secBit);
            dctInverse(index);
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
        jLabelImage2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabelImage1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jToggleButton_Embed = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        jLabelImage = new javax.swing.JLabel();
        jToggleButton_Extract1 = new javax.swing.JToggleButton();
        jToggleButton_Extract2 = new javax.swing.JToggleButton();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Original Image");

        jButton1.setText("Choose file");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabelImage2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel2.setText("Digital Image Watermarking using DCT");

        jButton2.setText("Choose file");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabelImage1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 36)); // NOI18N
        jLabel3.setText("+");

        jToggleButton_Embed.setText("Embed");
        jToggleButton_Embed.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_EmbedActionPerformed(evt);
            }
        });

        jLabel5.setText("Result");

        jLabelImage.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jToggleButton_Extract1.setText("Reset");

        jToggleButton_Extract2.setText("Extract");

        jLabel6.setText("Embed/Watermarked Image ");

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
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(8, 8, 8)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelImage1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(42, 42, 42)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addGap(44, 44, 44)
                                        .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jToggleButton_Embed, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jToggleButton_Extract1, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jToggleButton_Extract2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(23, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addGap(168, 168, 168)
                .addComponent(jLabelImage2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(jToggleButton_Extract1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToggleButton_Embed, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToggleButton_Extract2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel3)))
                .addGap(21, 21, 21)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelImage2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
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
            jLabelImage1.setIcon(resizePic(path));
            srcOriginalFile=path;
        }  
        else if (fileState==JFileChooser.ERROR_OPTION){
            System.err.println("Only image are allowed!!!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
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
            srcEmbedFile=path;

        }  
        else if (fileState==JFileChooser.ERROR_OPTION){
            JOptionPane.showMessageDialog(this, "Only image are allowed");
            System.err.println("[ERROR] Only image are allowed");
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jToggleButton_EmbedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_EmbedActionPerformed
        // TODO add your handling code here:
        
        if (srcEmbedFile.length()<=0||srcOriginalFile.length()<=0){
            JOptionPane.showMessageDialog(this, "Invalid image");
            System.err.println("[ERROR] Invalid image");
            return;
        }
        try {
            if (canWatermarking()){
                // can watermarking
                 devideIntoBlocks(srcOriginalFile);
                 dctForLoopSecret();
                 saveImageFile();
                //convertFile2Binary(path); 
                
                
            }
            else{
                JOptionPane.showMessageDialog(this, "Size of the embedded image is too large");
                System.err.println("[ERROR] Size of the embedded image is too large");
            }
        } catch (IOException ex) {
            Logger.getLogger(DCTWatermarking.class.getName()).log(Level.SEVERE, null, ex);
        }
        

    }//GEN-LAST:event_jToggleButton_EmbedActionPerformed

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
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabelImage;
    private javax.swing.JLabel jLabelImage1;
    private javax.swing.JLabel jLabelImage2;
    private javax.swing.JToggleButton jToggleButton_Embed;
    private javax.swing.JToggleButton jToggleButton_Extract1;
    private javax.swing.JToggleButton jToggleButton_Extract2;
    // End of variables declaration//GEN-END:variables
}
