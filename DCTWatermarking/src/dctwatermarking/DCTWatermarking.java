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
    public int xVal = 0;
    public int yVal = 0;
    public int red = 0;
    public int green = 0;
    public int blue = 0;

    // dct values
    public int dctVal = -999999; // set giá trị mặc định để kiểm tra xem sử dụng block nào
    public int rDctVal = 0;

    public Location() {
        red = 0;
        blue = 0;
        green = 0;
        xVal = 0;
        yVal = 0;
        dctVal = -999999;
        rDctVal = 0;
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
    public static double pi = 3.141592653589793238462643383279502884197169399375105820974944592307816406286208998628034825342117067982148086513282306647093844609550582231725359408128481;
    public Location[][][] blocks = new Location[100000][8][8];
    public int locaLenght = 1;

    // DCT alg parameter
    public int coorX = 3, coorY = 2, coorP = 3, coorQ = 3; // vi tri toa do cac diem giau anh
    public int difference;              // do sai lech
    public int t = 4;                     // dieu kien do chenh lech d
    public int a = 2 * (2 * t + 1);
    public String secretString = "";      // your bit 010100001010
    public int fileLength = 32;          // chen vao do dai cua file

    // image values
    public int width;
    public int height;

    public int coverwidth;
    public int coverheight;

    public int dctWidthBlock = 0;
    public int dctHeightBlock = 0;

    // file srce
    public String srcOriginalFile = "";
    public String srcEmbedFile = "";

    // program
    String textArea = "";

    public DCTWatermarking() {
        initComponents();
    }

    public ImageIcon resizePic(String picPath) {
        ImageIcon myImg = new ImageIcon(picPath);
        Image img = myImg.getImage().getScaledInstance(jLabelImage2.getWidth(), jLabelImage2.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon myPicture = new ImageIcon(img);
        return myPicture;
    }

    public void devideIntoBlocks(String path) throws IOException {
        File file = new File(path);
        BufferedImage img = ImageIO.read(file);
        width = img.getWidth();
        height = img.getHeight();
        dctWidthBlock = width / 8;
        dctHeightBlock = height / 8;

        int block = 0;
        int startX = 0, startY = 0;
        int y = 0, x = 0, i = 0, j = 0;

        while (y <= dctHeightBlock * n) {
            while (x <= dctWidthBlock * n) {
                while (j < 8) {
                    while (i < 8) {
                        blocks[block][j][i] = new Location();
                        // set color of image
                        blocks[block][j][i].setRed(new Color(img.getRGB(x + i, y + j), true).getRed());
                        blocks[block][j][i].setGreen(new Color(img.getRGB(x + i, y + j), true).getGreen());
                        blocks[block][j][i].setBlue(new Color(img.getRGB(x + i, y + j), true).getBlue());
                        // set image coordinates
                        blocks[block][j][i].setxVal(x + i);
                        blocks[block][j][i].setyVal(y + j);
                        i++;
                        locaLenght++;
                    }
                    i = 0;
                    j++;
                }
                j = 0;
                // go to next block
                block++;
                if (x + 8 <= dctWidthBlock * n - 8) {
                    x = x + 8;
                } else {
                    x = 0;
                    y = y + 8;
                }
                if (y + 8 > dctHeightBlock * n) {
                    // for debug
                    System.out.println("[INFO] Read file okay");
                    return;
                }
            }
        }
    }

    public int getImageValue(int block, int x, int y) {
        int tmp;
        if (blocks[block][x][y].getDctVal() != -999999) {
            tmp = (int) ((blocks[block][x][y].getrDctVal() > 255) ? 255 : blocks[block][x][y].getrDctVal());
            tmp = (tmp < 0) ? 0 : tmp;
            return tmp;
        } else {
            return blocks[block][x][y].getBlue();
        }

    }

    public boolean canWatermarking() throws IOException {
        // Original Image
        File file = new File(srcOriginalFile);
        BufferedImage img = ImageIO.read(file);
        width = img.getWidth();
        height = img.getHeight();

        dctWidthBlock = width / 8;
        dctHeightBlock = height / 8;

        int lenght1 = dctHeightBlock * dctWidthBlock;

        // Embed Image
        int lenght2 = convertFile2Binary(srcEmbedFile);
        //System.out.println("Size in canWatermarking is "+lenght2);
        if (lenght1 >= lenght2 + fileLength) {
            return true;
        } else {
            return false;
        }
    }

    public BufferedImage readRestData(BufferedImage image) throws IOException {
        // chieu dung
        File file = new File(srcOriginalFile);
        BufferedImage img = ImageIO.read(file);

        for (int i = 1; i < height; i++) {
            for (int j = dctWidthBlock * 8; j < width; j++) {
                // System.out.println(j+" "+i);
                int pixel = img.getRGB(j, i);
                image.setRGB(j, i, pixel);
            }
        }

        for (int i = dctHeightBlock * 8; i < height; i++) {
            for (int j = 1; j < width; j++) {
                // System.out.println(j+" "+i);
                int pixel = img.getRGB(j, i);
                image.setRGB(j, i, pixel);
            }
        }
        return image;
    }

    public void saveImageFile() throws IOException {
        int ll = 0;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int block = 0; block < blocks.length; block++) {
            // check is blocks[block][k][l] is s instanceof Simple1
            if (!(blocks[block][0][0] instanceof Location)) {
                break;
            }

            for (int k = 0; k < 8; k++) {
                for (int l = 0; l < 8; l++) {
                    Color color1 = new Color(blocks[block][k][l].getRed(), blocks[block][k][l].getGreen(), getImageValue(block, k, l));
                    int rgb = color1.getRGB();
                    image.setRGB(blocks[block][k][l].getxVal(), blocks[block][k][l].getyVal(), rgb);
                }
            }
        }

        image = readRestData(image);
        File outputFile = new File("output.png");
        ImageIO.write(image, "png", outputFile);

        System.out.println("[INFO] Done");

    }

    public void dctTransform(int block) {
        int i, j, k, l;

        // dct will store the discrete cosine transform
        double ci, cj, dct1, sum;

        for (i = 0; i < m; i++) {
            for (j = 0; j < n; j++) {
                // ci and cj depends on frequency as well as
                // number of row and columns of specified matrix
                if (i == 0) {
                    ci = 1 / Math.sqrt(m);
                } else {
                    ci = Math.sqrt(2) / Math.sqrt(m);
                }

                if (j == 0) {
                    cj = 1 / Math.sqrt(n);
                } else {
                    cj = Math.sqrt(2) / Math.sqrt(n);
                }

                // sum will temporarily store the sum of 
                // cosine signals
                sum = 0;
                for (k = 0; k < m; k++) {
                    for (l = 0; l < n; l++) {
                        dct1 = (blocks[block][k][l].getBlue())
                                * Math.cos((2 * k + 1) * i * pi / (2 * m))
                                * Math.cos((2 * l + 1) * j * pi / (2 * n));
                        sum = sum + dct1;
                    }
                }
                blocks[block][i][j].setDctVal((int) (ci * cj * sum));
                if (i == 0 && j == 0) {
                    blocks[block][i][j].setDctVal(blocks[block][i][j].getDctVal());
                }
                //System.out.print(blocks[block][i][j].getDctVal()+" ");
            }
        }
    }

    public void dctInverse(int block) {
        int i, j, k, l;

        // dct inverse transform alg
        double ci, cj, dct1, sum;

        for (k = 0; k < m; k++) {
            for (l = 0; l < n; l++) {
                sum = 0;
                for (i = 0; i < m; i++) {
                    for (j = 0; j < n; j++) {
                        // ci and cj depends on frequency as well as
                        // number of row and columns of specified matrix
                        if (i == 0) {
                            ci = 1 / Math.sqrt(m);
                        } else {
                            ci = Math.sqrt(2) / Math.sqrt(m);
                        }

                        if (j == 0) {
                            cj = 1 / Math.sqrt(n);
                        } else {
                            cj = Math.sqrt(2) / Math.sqrt(n);
                        }
                        // sum will temporarily store the sum of 
                        // cosine signals

                        dct1 = ci * cj * blocks[block][i][j].getDctVal()
                                * Math.cos((2 * k + 1) * i * pi / (2 * m))
                                * Math.cos((2 * l + 1) * j * pi / (2 * n));
                        sum = sum + dct1;
                    }
                }
                blocks[block][k][l].setrDctVal((int) (sum));

            }

        }
    }

    public int convertFile2Binary(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedImage img = ImageIO.read(file);
        int width = img.getWidth();
        int height = img.getHeight();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(img.getRGB(j, i));
                if (c.getBlue() == 0) {
                    secretString += "0";
                } else {
                    secretString += "1";
                }
            }
        }
        return secretString.length();
    }

    public String convertString2Binary(String input) {
        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar)) // char -> int, auto-cast
                            .replaceAll(" ", "0") // zero pads
            );
        }
        return result.toString();
    }

    public void chooseCoefficient(int block) {//chon he so

        boolean isValid = false;

        // d= || b „(i,j) - b „(p,q) || mod a
        // tính do chenh lenh
        // a=2(2t+1)
        difference = (int) (Math.abs(Math.abs(blocks[block][coorX][coorY].getDctVal()) - Math.abs(blocks[block][coorP][coorQ].getDctVal())) % a);
    }

    public boolean isBigger(double num1, double num2) {
        return Math.abs(num1) >= Math.abs(num2);
    }

    public void dctWatermarking(int block, int bitVal) {
        // nhung cac bit vao
        // thoa man cac dieu kien
        // d > 2t+1 neu s=1
        // d < 2t+1 neu s=0
        if ((difference < (2 * t + 1)) && (bitVal == 1)) {
            if (isBigger(blocks[block][coorX][coorY].getDctVal(), blocks[block][coorP][coorQ].getDctVal())) {
                if (blocks[block][coorX][coorY].getDctVal() >= 0) {
                    blocks[block][coorX][coorY].setDctVal(blocks[block][coorX][coorY].getDctVal() + ((int) (0.75 * a) - difference));
                } else {
                    blocks[block][coorX][coorY].setDctVal(blocks[block][coorX][coorY].getDctVal() - ((int) (0.75 * a) - difference));
                }
            } else {
                if (blocks[block][coorP][coorQ].getDctVal() >= 0) {
                    blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal() + ((int) (0.75 * a) - difference));
                } else {
                    blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal() - ((int) (0.75 * a) - difference));
                }
            }
        }

        if ((difference >= (2 * t + 1)) && (bitVal == 0)) {
            if (isBigger(blocks[block][coorX][coorY].getDctVal(), blocks[block][coorP][coorQ].getDctVal())) {
                if (blocks[block][coorX][coorY].getDctVal() >= 0) {
                    blocks[block][coorX][coorY].setDctVal(blocks[block][coorX][coorY].getDctVal() - Math.abs(difference - (int) (0.25 * a)));
                } else {
                    blocks[block][coorX][coorY].setDctVal(blocks[block][coorX][coorY].getDctVal() + (difference - (int) (0.25 * a)));
                }
            } else {
                if (blocks[block][coorP][coorQ].getDctVal() >= 0) {
                    blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal() - Math.abs(difference - (int) (0.25 * a)));
                } else {
                    blocks[block][coorP][coorQ].setDctVal(blocks[block][coorP][coorQ].getDctVal() + (difference - (int) (0.25 * a)));
                }
            }
        }

    }

    public void dctForLoopSecret() {
        //System.out.println("secretString = "+secretString);
        int index = 0;
        int secLength = secretString.length();
        //System.out.println("secLength = "+secLength);
        int secBit = 0;
        //maximum 128x128
        BufferedImage bimg = null;
        try {
            bimg = ImageIO.read(new File(srcEmbedFile));
        } catch (IOException e) {
            System.out.println("Error when read file");
        }

        coverwidth = bimg.getWidth();
        coverheight = bimg.getHeight();
        String tmp = Integer.toBinaryString(coverwidth);

        for (int i = tmp.length(); i < fileLength / 2; i++) {
            tmp = "0" + tmp;
        }
        String tmp1 = "";
        tmp1 = Integer.toBinaryString(coverheight);
        for (int i = tmp1.length(); i < fileLength / 2; i++) {
            tmp1 = "0" + tmp1;
        }
        tmp = tmp + tmp1;
        // loop throught secret
        //System.out.println("tmp = "+tmp);
        secretString = tmp + secretString; // thêm độ dài của file vào
        secLength = secretString.length();
        //System.out.println("secretString = "+secretString);

        while (index < secLength) {
            secBit = secretString.charAt(index) - 48;
            //System.out.println("Bit "+secBit);
            dctTransform(index);
            chooseCoefficient(index);
            dctWatermarking(index, secBit);
            dctInverse(index);
            index++;
        }
    }

    // DECODE 
    public int booleansToInt(boolean[] arr, int s) {
        int n = 0;
        if (s == 1) {
            for (int i = 0; i < fileLength / 2; i++) {
                n = (n << 1) | (arr[i] ? 1 : 0);
            }
        } else {
            for (int i = fileLength / 2; i < fileLength; i++) {
                n = (n << 1) | (arr[i] ? 1 : 0);
            }
        }
        return n;
    }

    public void containWatermarking() throws IOException {
        int index = 0;
        //File file= new File(srcOriginalFile);
        try {
            devideIntoBlocks(srcOriginalFile);
        } catch (IOException ioe) {
            System.out.println("Error on devide into many block data");
        }

        boolean[] secretLengthArray = new boolean[fileLength];

        while (index < fileLength) {
            dctTransform(index);
            chooseCoefficient(index);
            //System.out.println(difference);
            if (difference < (2 * t + 1)) {
                secretLengthArray[index] = false;
            } else {
                secretLengthArray[index] = true;
            }
            dctInverse(index);
            //System.out.print(secretLengthArray[index]);
            index++;

        }
        //lay 2 gia tri width va height ra
        int wtmkWidth = booleansToInt(secretLengthArray, 1);
        int wtmkHeight = booleansToInt(secretLengthArray, 2);
        int secretLength = wtmkWidth * wtmkHeight;
        //sau do tiep tuc xu ly du lieu
        boolean[] watermarkData = new boolean[secretLength];
        char[] watermarkData2 = new char[secretLength];
        index = 32;
        for (int i = 0; index < secretLength;) {
            //System.out.println("Index "+index);
            dctTransform(index);
            chooseCoefficient(index);
            if (difference < (2 * t + 1)) {
                watermarkData[i] = false;
                watermarkData2[i] = '0';
            } else {
                watermarkData[i] = true;
                watermarkData2[i] = '1';
            }
            dctInverse(index);
            //if(i==685-32) System.out.println(watermarkData[i]+" "+difference);
            i++;
            index++;
        }
        //ghi ra file, dung ImageIO
        BufferedImage wtmkImage = new BufferedImage(wtmkWidth, wtmkHeight, BufferedImage.TYPE_INT_RGB);
        //Color black =new Color(255,255,255);
        for (int i = 0; i < wtmkHeight; i++) {
            for (int j = 0; j < wtmkWidth; j++) {
                //System.out.println(j+" "+i);
                if (watermarkData[j + wtmkHeight * i]) {
                    int rgb = 255;
                    rgb = (rgb << 8) + 255;
                    rgb = (rgb << 8) + 255;
                    wtmkImage.setRGB(j, i, rgb);
                } else {
                    wtmkImage.setRGB(j, i, 0);
                }
            }
        }
        ImageIO.write(wtmkImage, "png", new File("recoverwtkmk.png"));
        jLabelImage2.setIcon(resizePic("recoverwtkmk.png"));
        System.out.println("Done in recover Watermarking");
    }

    public byte[] toByteArray(boolean[] bool) {
        int size = bool.length;
        int e = size / 8;
        byte[] result = new byte[e];
        for (int i = 0; i < e; i++) {
            byte b = (byte) ((bool[0 + 8 * i] ? 1 << 7 : 0) + (bool[1 + 8 * i] ? 1 << 6 : 0) + (bool[2 + 8 * i] ? 1 << 5 : 0)
                    + (bool[3 + 8 * i] ? 1 << 4 : 0) + (bool[4 + 8 * i] ? 1 << 3 : 0) + (bool[5 + 8 * i] ? 1 << 2 : 0)
                    + (bool[6 + 8 * i] ? 1 << 1 : 0) + (bool[7 + 8 * i] ? 1 : 0));

            result[i] = b;
        }
        return result;
    }

    public void saveWatermarkedFile(boolean[] bools) {

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
        jLabelImage2 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabelImage1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jToggleButton_Embed = new javax.swing.JToggleButton();
        jLabel5 = new javax.swing.JLabel();
        jLabelImage = new javax.swing.JLabel();
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

        jToggleButton_Extract2.setText("Extract");
        jToggleButton_Extract2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_Extract2ActionPerformed(evt);
            }
        });

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
                                .addComponent(jLabelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(42, 42, 42)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(34, 34, 34)
                                        .addComponent(jLabel3)
                                        .addGap(52, 52, 52)
                                        .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(54, 54, 54)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jToggleButton_Embed, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                        .addGap(49, 49, 49)
                        .addComponent(jToggleButton_Embed, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToggleButton_Extract2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelImage, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(67, 67, 67)
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
        JFileChooser filec = new JFileChooser();
        filec.setCurrentDirectory(new File(System.getProperty("user.home")));
        // check file extension

        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("*.Images", "jpg", "jpeg", "png");
        filec.addChoosableFileFilter(fileFilter);

        int fileState = filec.showSaveDialog(null);

        // if the user select a file
        if (fileState == JFileChooser.APPROVE_OPTION) {
            File selectedFile = filec.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            // display the image in the jlanel
            jLabelImage1.setIcon(resizePic(path));
            srcOriginalFile = path;
            //System.out.println(path);
        } else if (fileState == JFileChooser.ERROR_OPTION) {
            System.err.println("Only image are allowed!!!");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        JFileChooser filec = new JFileChooser();
        filec.setCurrentDirectory(new File(System.getProperty("user.home")));
        // check file extension

        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("*.Images", "jpg", "jpeg", "png");
        filec.addChoosableFileFilter(fileFilter);

        int fileState = filec.showSaveDialog(null);

        // if the user select a file
        if (fileState == JFileChooser.APPROVE_OPTION) {
            File selectedFile = filec.getSelectedFile();
            String path = selectedFile.getAbsolutePath();

            // display the image in the jlanel
            jLabelImage.setIcon(resizePic(path));
            srcEmbedFile = path;

        } else if (fileState == JFileChooser.ERROR_OPTION) {
            JOptionPane.showMessageDialog(this, "Only image are allowed");
            System.err.println("[ERROR] Only image are allowed");
        }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jToggleButton_EmbedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_EmbedActionPerformed
        // TODO add your handling code here:

        if (srcEmbedFile.length() <= 0 || srcOriginalFile.length() <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid image");
            System.err.println("[ERROR] Invalid image");
            return;
        }
        try {
            if (canWatermarking()) {
                // can watermarking
                devideIntoBlocks(srcOriginalFile);
                dctForLoopSecret();
                saveImageFile();
                jLabelImage2.setIcon(resizePic("output.png"));
            } else {
                JOptionPane.showMessageDialog(this, "Size of the embedded image is too large");
                System.err.println("[ERROR] Size of the embedded image is too large");
            }
        } catch (IOException ex) {
            Logger.getLogger(DCTWatermarking.class.getName()).log(Level.SEVERE, null, ex);
        }


    }//GEN-LAST:event_jToggleButton_EmbedActionPerformed

    private void jToggleButton_Extract2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_Extract2ActionPerformed
        try {
            containWatermarking();

        } catch (IOException ex) {
            Logger.getLogger(DCTWatermarking.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jToggleButton_Extract2ActionPerformed

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
    private javax.swing.JToggleButton jToggleButton_Extract2;
    // End of variables declaration//GEN-END:variables
}
