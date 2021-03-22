/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ups.vista;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author erleo15
 */
public class Principal extends javax.swing.JFrame {

    File seleccion = null;
    private boolean verificador1 = false, verificador2 = false, verificador3 = false, verificador4 = false;

    /**
     * Creates new form Principal
     */
    public Principal() {
        initComponents();

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jtxtOutput = new javax.swing.JTextArea();
        txtLink = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnConfigurar = new javax.swing.JButton();
        lblArchivoDirectorio = new javax.swing.JLabel();
        txtArchivo = new javax.swing.JTextField();
        btnSeleccionar = new javax.swing.JButton();
        lblNumeroLineas = new javax.swing.JLabel();
        txtNumeroLineas = new javax.swing.JTextField();
        btnAgregarCola = new javax.swing.JButton();
        chbkDirectorio = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnEjecutarHerramienta = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        btnBackend = new javax.swing.JButton();
        btnFrontend = new javax.swing.JButton();
        btnShowFrontEnd = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(226, 212, 87));

        jLabel2.setFont(new java.awt.Font("Ubuntu", 1, 24)); // NOI18N
        jLabel2.setText("Configuracion");

        jtxtOutput.setColumns(20);
        jtxtOutput.setRows(5);
        jScrollPane1.setViewportView(jtxtOutput);

        txtLink.setEditable(false);
        txtLink.setText("https://commoncrawl.s3.amazonaws.com/");
        txtLink.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                txtLinkMouseClicked(evt);
            }
        });

        jLabel3.setText("Link");

        btnConfigurar.setText("Configurar link");
        btnConfigurar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfigurarActionPerformed(evt);
            }
        });

        lblArchivoDirectorio.setText("Seleccione el archivo");

        txtArchivo.setEditable(false);

        btnSeleccionar.setText("Seleccionar");
        btnSeleccionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSeleccionarActionPerformed(evt);
            }
        });

        lblNumeroLineas.setText("Numero de Lineas");

        txtNumeroLineas.setText("2");
        txtNumeroLineas.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumeroLineasKeyTyped(evt);
            }
        });

        btnAgregarCola.setText("Agregar a la cola");
        btnAgregarCola.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarColaActionPerformed(evt);
            }
        });

        chbkDirectorio.setText("Directorio");
        chbkDirectorio.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbkDirectorioItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(259, 259, 259)
                        .addComponent(jLabel2))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel3)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtLink, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblNumeroLineas)
                                        .addGap(93, 93, 93)
                                        .addComponent(txtNumeroLineas, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnConfigurar))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(lblArchivoDirectorio, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSeleccionar))
                            .addComponent(chbkDirectorio)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(286, 286, 286)
                        .addComponent(btnAgregarCola)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(jLabel2)
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtLink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(btnConfigurar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(chbkDirectorio)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblArchivoDirectorio)
                    .addComponent(txtArchivo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSeleccionar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumeroLineas)
                    .addComponent(txtNumeroLineas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addComponent(btnAgregarCola)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(39, 186, 153));

        jLabel1.setFont(new java.awt.Font("Ubuntu", 1, 24)); // NOI18N
        jLabel1.setText("Ejecucion por archivo");

        btnEjecutarHerramienta.setText("Ejecutar");
        btnEjecutarHerramienta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEjecutarHerramientaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEjecutarHerramienta)
                .addGap(279, 279, 279))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(jLabel1)
                .addContainerGap(325, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(btnEjecutarHerramienta)
                .addContainerGap(85, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(197, 216, 53));

        jLabel4.setFont(new java.awt.Font("Ubuntu", 1, 36)); // NOI18N
        jLabel4.setText("Resultados");

        btnBackend.setText("1. Ejecutar Backend");
        btnBackend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackendActionPerformed(evt);
            }
        });

        btnFrontend.setText("2. Ejecutar Frontend");
        btnFrontend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFrontendActionPerformed(evt);
            }
        });

        btnShowFrontEnd.setText("3. Mostrar  Frontend");
        btnShowFrontEnd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnShowFrontEndActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addGap(115, 115, 115))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(54, 54, 54)
                        .addComponent(btnBackend)
                        .addGap(18, 18, 18)
                        .addComponent(btnFrontend))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(172, 172, 172)
                        .addComponent(btnShowFrontEnd)))
                .addContainerGap(120, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnBackend)
                    .addComponent(btnFrontend))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnShowFrontEnd)
                .addGap(28, 28, 28))
        );

        jButton1.setText("jButton1");
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
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(32, 32, 32)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 305, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(140, 140, 140))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(184, 184, 184))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfigurarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfigurarActionPerformed
        // TODO add your handling code here:
        if (!verificarSeleccion()) {
            //JOptionPane.showMessageDialog(null, "Atencion, proyecto no localizado. Por favor pulse en localizar proyecto para indicar la carpeta contenedora.");
            seleccionarProyecto();
            if (!verificarSeleccion()) {
                return;
            }
        }
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;
        String lineas = "";
        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            ///home/erleo15/pasantias/DanielOrtega/AMEF/src/main/resources
            archivo = new File(seleccion.getAbsolutePath() + "/src/main/resources/amef.properties");
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            String lastLine = "";

            while ((linea = br.readLine()) != null) {
                lineas += linea + "\n";
                if (!linea.isEmpty()) {
                    System.out.println(linea);
                    lastLine = linea;
                }
            }

            String corte[] = lastLine.split("=");
            String link = corte[1].trim();
            String nuevoLink = txtLink.getText();
            lineas = lineas.replaceAll(link, nuevoLink);
            System.out.println(lineas);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hubo un problema", "Error.", JOptionPane.ERROR_MESSAGE);
            seleccion = null;
            return;
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                JOptionPane.showMessageDialog(null, "Hubo un problema", "Error.", JOptionPane.ERROR_MESSAGE);
                seleccion = null;
                return;
            }
        }

        File file = archivo;
        file.delete();
        try {
            file.createNewFile();

            FileWriter f2 = new FileWriter(file, false);
            f2.write(lineas);
            f2.close();
            lineas = "";
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Hubo un problema", "Error.", JOptionPane.ERROR_MESSAGE);
            seleccion = null;
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(null, "Desea aplicar los cambios ahora?");

        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }
        ejecutar("cd " + seleccion.getAbsolutePath() + " && mvn package");
    }//GEN-LAST:event_btnConfigurarActionPerformed

    private void txtLinkMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_txtLinkMouseClicked
        // TODO add your handling code here:
        if (seleccion == null) {
            JOptionPane.showMessageDialog(null, "Atencion, proyecto no localizado. Por favor pulse en localizar proyecto para indicar la carpeta contenedora.");
            txtLink.setEditable(false);
        } else
            txtLink.setEditable(true);
    }//GEN-LAST:event_txtLinkMouseClicked

    private void chbkDirectorioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbkDirectorioItemStateChanged
        // TODO add your handling code here:
        if (!chbkDirectorio.isSelected()) {
            lblNumeroLineas.setText("Numero de Lineas");
            lblArchivoDirectorio.setText("Seleccione el archivo");
            txtArchivo.setEnabled(true);
        } else {
            lblNumeroLineas.setText("Numero de lineas \npor archivo");
            //lblArchivoDirectorio.setText("Seleccione el directorio");
            txtArchivo.setText("");
            txtArchivo.setEnabled(false);
        }
    }//GEN-LAST:event_chbkDirectorioItemStateChanged

    private void btnAgregarColaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarColaActionPerformed
        // TODO add your handling code here:
        if (!verificarSeleccion()) {
            seleccionarProyecto();
        }
        String comando = "";
        if (txtArchivo.getText().isEmpty() && !chbkDirectorio.isSelected()) {
            JOptionPane.showMessageDialog(null, "Seleccione el archivo de texto a procesar");
            return;
        }

        if (txtNumeroLineas.getText().isEmpty() && !chbkDirectorio.isSelected()) {
            int opcion = JOptionPane.showConfirmDialog(null, "Atencion no se ha indicado el numero de lineas.\n Desea agregar todas las lineas del archivo a la cola?");

            if (opcion != JOptionPane.OK_OPTION) {
                return;
            }
            comando = "cd " + seleccion.getAbsolutePath() + " && ./bin/master queue -f " + txtArchivo.getText();
            System.out.println(comando);

        } else {
            comando = "cd " + seleccion.getAbsolutePath() + " && ./bin/master queue -f " + txtArchivo.getText() + " -l " + txtNumeroLineas.getText();
            System.out.println(comando);
        }

        if (chbkDirectorio.isSelected()) {

            if (txtNumeroLineas.getText().isEmpty()) {
                int opcion = JOptionPane.showConfirmDialog(null, "Atencion no se ha indicado el numero de lineas.\n Desea agregar todas las lineas de los archivos a la cola?");

                if (opcion != JOptionPane.OK_OPTION) {
                    return;
                }
                comando = "cd " + seleccion.getAbsolutePath() + " && ./bin/queue ";
            } else {
                comando = "cd " + seleccion.getAbsolutePath() + " && ./bin/queue " + txtNumeroLineas.getText();

            }

        }

        JOptionPane.showMessageDialog(null, comando);
        ejecutar(comando);//linea donde se va a ejecutar el comando de linux

    }//GEN-LAST:event_btnAgregarColaActionPerformed

    private void txtNumeroLineasKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumeroLineasKeyTyped
        // TODO add your handling code here:
        char caracter = evt.getKeyChar();

        // Verificar si la tecla pulsada no es un digito
        if (((caracter < '0')
                || (caracter > '9'))
                && (caracter != '\b' /*corresponde a BACK_SPACE*/)) {
            evt.consume();  // ignorar el evento de teclado
        }
    }//GEN-LAST:event_txtNumeroLineasKeyTyped

    private void btnSeleccionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSeleccionarActionPerformed
        // TODO add your handling code here:
        JFileChooser fileChooser = new JFileChooser();
        if (chbkDirectorio.isSelected()) {
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else {
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        }
        fileChooser.showOpenDialog(this);
        try {
            txtArchivo.setText(fileChooser.getSelectedFile().getAbsolutePath());
        } catch (Exception e) {

        }
    }//GEN-LAST:event_btnSeleccionarActionPerformed
    Hilo h1 = new Hilo("comando", "hilo Ejecutar ");
    Thread hilo1 = new Thread(h1);
    private void btnEjecutarHerramientaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEjecutarHerramientaActionPerformed
        // TODO add your handling code here:

        if (!verificarSeleccion()) {
            seleccionarProyecto();
        }
        try{
        if (!verificador1) {
            h1.setComando("cd " + seleccion.getAbsolutePath() + " && ./bin/master run ");
            hilo1 = new Thread(h1);
            hilo1.start();
            verificador1 = true;
            btnEjecutarHerramienta.setText("Detener ejcucion de la herramienta");
        } else {
            hilo1.stop();
            verificador1 = false;
            btnEjecutarHerramienta.setText("Ejecutar Herramienta");
        }
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Ocurrio un error "+e.getMessage());
        }
    }//GEN-LAST:event_btnEjecutarHerramientaActionPerformed
    Hilo h2 = new Hilo("comando", "hilo Encender Backend ");
    Thread hilo2 = new Thread(h2);
    private void btnBackendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackendActionPerformed
        // TODO add your handling code here:
        if (!verificador2) {
            hilo2 = new Thread(h2);
            hilo2.start();
            verificador2 = true;
            btnBackend.setText("Detener Ejecucion Backend");
        } else {
            hilo2.stop();
            verificador2 = false;
            btnBackend.setText("1. Ejecutar Backend");
        }
    }//GEN-LAST:event_btnBackendActionPerformed
    Hilo h3 = new Hilo("comando", "hilo Encender Frontend");
    Thread hilo3 = new Thread(h3);
    private void btnFrontendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFrontendActionPerformed
        // TODO add your handling code here:

        if (!verificador3) {
            hilo3 = new Thread(h3);
            hilo3.start();
            verificador3 = true;
        } else {
            hilo3.stop();
            verificador3 = false;
        }
    }//GEN-LAST:event_btnFrontendActionPerformed

    public void goToURL(String URL) {
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();

            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                try {
                    java.net.URI uri = new java.net.URI(URL);
                    desktop.browse(uri);
                 
                } catch (URISyntaxException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private void btnShowFrontEndActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnShowFrontEndActionPerformed
        // TODO add your handling code here:
        // goToURL("http://localhost:4200/");
        goToURL("https://google.com");
    }//GEN-LAST:event_btnShowFrontEndActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        
        try {
            // TODO add your handling code here:
            java.awt.Desktop.getDesktop().open(new File("terminal ping google.com"));
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
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
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }

    public void seleccionarProyecto() {
        JOptionPane.showMessageDialog(null, "Por favor seleccione la carpeta en donde se encuentra la herramienta AMEF");

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fileChooser.showOpenDialog(this);
        this.seleccion = fileChooser.getSelectedFile();
    }

    public boolean verificarSeleccion() {
        return seleccion != null;
    }

    public void ejecutar(String comando) {
        jtxtOutput.setText("");
        JOptionPane.showMessageDialog(null, "Espere un momento por favor......\n");
        String s = null;

        try {

            String[] cmd = {"/bin/bash", "-c", comando};
            // Ejcutamos el comando
            Process p = Runtime.getRuntime().exec(cmd);

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new InputStreamReader(
                    p.getErrorStream()));

            // Leemos la salida del comando
            System.out.println("Ésta es la salida standard del comando:\n");
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
                jtxtOutput.append(s + "\n");
            }

            // Leemos los errores si los hubiera
            System.out
                    .println("Ésta es la salida standard de error del comando (si la hay):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
                jtxtOutput.append(s + "\n");
            }

        } catch (IOException e) {
            System.out.println("Excepción: ");
            e.printStackTrace();
            //System.exit(-1);
        }

        JOptionPane.showMessageDialog(null, "Se termino de ejecutar el proceso.");

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCola;
    private javax.swing.JButton btnBackend;
    private javax.swing.JButton btnConfigurar;
    private javax.swing.JButton btnEjecutarHerramienta;
    private javax.swing.JButton btnFrontend;
    private javax.swing.JButton btnSeleccionar;
    private javax.swing.JButton btnShowFrontEnd;
    private javax.swing.JCheckBox chbkDirectorio;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jtxtOutput;
    private javax.swing.JLabel lblArchivoDirectorio;
    private javax.swing.JLabel lblNumeroLineas;
    private javax.swing.JTextField txtArchivo;
    private javax.swing.JTextField txtLink;
    private javax.swing.JTextField txtNumeroLineas;
    // End of variables declaration//GEN-END:variables
}
