/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utez.edu.modelo.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import utez.edu.modelo.bean.BeanNomina;
import utez.edu.mx.utilerias.Conexion;

/**
 *
 * @author Esmeralda
 * @version 1 09/04/2019
 */
public class DaoNomina {

    private ResultSet rs;
    private PreparedStatement psm;
    private Connection con;
    private CallableStatement csm;
    private boolean resultado;
    
    public BeanNomina consultarNomina(int semana, int idUsuario){
        BeanNomina beanNomina= null;
        try{
            con = Conexion.getConexion();
            psm = con.prepareCall("select * from nomina where idUsuario=? and semana=?");
            psm.setInt(1, idUsuario);
            psm.setInt(2, semana);
            rs = psm.executeQuery();
            if(rs.next()){
                beanNomina = new BeanNomina();
                beanNomina.setIdNomina(rs.getInt("idNomina"));
                beanNomina.setFecha(rs.getString("fecha"));
                beanNomina.setIdProyecto(rs.getInt("idProyecto"));
                beanNomina.setIdUsuario(rs.getInt("idUsuario"));
                beanNomina.setSemana(rs.getInt("semana"));
                beanNomina.setPagado(rs.getInt("pagado"));
                beanNomina.setValorGanado(rs.getDouble("valorGanado"));
            }
        }catch(SQLException ex){
            System.out.println("Erroe en DaoNomina consultarNomina()");
        }finally{
            try{
                con.close();
                psm.close();
                rs.close();
            }catch(SQLException e){
                System.out.println("Erroe en DaoNomina consultarNomina()-cierre");
            }
        }
        return beanNomina;
    }
    
    public boolean pagarNomina(int idUsuario,int idProyecto, String fecha, int semana,double valor){
        try{
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_pagarNomina (?,?,?,?,?)}");
            csm.setInt(1, idUsuario);
            csm.setInt(2, idProyecto);
            csm.setString(3, fecha);
            csm.setInt(4, semana);
            csm.setDouble(5, valor);
            resultado = csm.executeUpdate()==1;
        }catch(SQLException e){
            System.out.println("Error DaoNomina pagarNomina()"+e);
        }finally{
            try{
                con.close();
                csm.close();
            }catch(SQLException ex){
                 System.out.println("Error DaoNomina pagarNomina()-cierre"+ex);
            }
        }
        return resultado;
    }
    
    public static void main(String[] args) {
        DaoNomina daoNomina = new DaoNomina();
        System.out.println(daoNomina.pagarNomina(6, 1, "2019-04-10", 0, 10));
                
    }
    
}
