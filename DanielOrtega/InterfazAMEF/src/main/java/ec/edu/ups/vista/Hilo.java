/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.edu.ups.vista;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author erleo15
 */
public class Hilo implements Runnable{

    private String comando;
    private String nombre;

    public Hilo() {
    }

    public Hilo(String comando,String nombre) {
        this.comando = comando;
        this.nombre = nombre;
    }

    public String getComando() {
        return comando;
    }

    public void setComando(String comando) {
        this.comando = comando;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
    @Override
    public void run() { 
        
        JOptionPane.showMessageDialog(null,"Ejecutando el proceso......\n");
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
            }

            // Leemos los errores si los hubiera
            System.out
                    .println("Ésta es la salida standard de error del comando (si la hay):\n");
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }

        } catch (IOException e) {
            System.out.println("Excepción: ");
            e.printStackTrace();
            //System.exit(-1);
        }
        
        JOptionPane.showMessageDialog(null, "Se termino de ejecutar el proceso.");
        /*
        for(int i =0;i<50;i++){
            try {
                Thread.sleep(1000);
                System.out.println(i+", "+nombre);
            } catch (InterruptedException ex) {
                Logger.getLogger(Hilo.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
    }
    
}
