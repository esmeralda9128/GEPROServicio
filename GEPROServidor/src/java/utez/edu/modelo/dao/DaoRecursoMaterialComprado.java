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
import java.util.ArrayList;
import java.util.List;
import utez.edu.modelo.bean.BeanRecursoComprado;
import utez.edu.modelo.bean.BeanRecursoMaterial;
import utez.edu.mx.utilerias.Conexion;

/**
 *
 * @author horo_
 */
public class DaoRecursoMaterialComprado {
      private ResultSet rs;
    private PreparedStatement psm;
    private Connection con;
    private CallableStatement csm;
    private boolean resultado;
    
    
    public List<BeanRecursoComprado> buscarRecursoComprado(int idProyecto, int semana){
        List<BeanRecursoComprado> recursos = new ArrayList<>();
        BeanRecursoComprado recurso;
        DaoRecursoMaterial daoRecursoMaterial = new DaoRecursoMaterial();
         try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from recursoComprado where idProyecto=? and semana=?");
            psm.setInt(1, idProyecto);
            psm.setInt(2, semana);
            rs = psm.executeQuery();
            while (rs.next()) {
             recurso = new BeanRecursoComprado();
             recurso.setIdRecursoCom(rs.getInt("idRecursoComprado"));
             recurso.setFecha(rs.getString("fecha"));
             recurso.setIdProyecto(rs.getInt("idProyecto"));
             BeanRecursoMaterial recursoMaterial = daoRecursoMaterial.buscarRecursoComprado(rs.getInt("idRecursosMateriales"));
             recurso.setMateriales(recursoMaterial);
             recursos.add(recurso);
            }
         }catch(SQLException ex){
             System.out.println("Error DaoRecursoMaterialComprado buscarRecursoComprado" + ex);
         }finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoRecursoMaterialComprado buscarRecursoComprado()cerrar" + ex);
            }
        }
        return recursos;
    }
    
    public static void main(String[] args) {
        DaoRecursoMaterialComprado comprado = new DaoRecursoMaterialComprado();
        List<BeanRecursoComprado> comprados = comprado.buscarRecursoComprado(1, 0);
        
        for (int i = 0; i < comprados.size(); i++) {
            System.out.println( comprados.get(i).getMateriales().getNombreRecursoMat());
        }
        
        
    }

}
