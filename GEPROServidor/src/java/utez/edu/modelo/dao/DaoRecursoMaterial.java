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
import utez.edu.modelo.bean.BeanRecursoMaterial;
import utez.edu.mx.utilerias.Conexion;

/**
 *
 * @author Esmeralda 
 * @version 1 15/03/2019
 */
public class DaoRecursoMaterial {


    private ResultSet rs;
    private PreparedStatement psm;
    private Connection con;
    private CallableStatement csm;
    private boolean resultado;
/**
 * Método que regresa todos los recursos de un proyecto 
 * @param idProyecto el id del proyecto del que se necesita la información
 * @return regresa la lista de los recursos materiales del proyecto
 */
    public List<BeanRecursoMaterial> listaRecursos(int idProyecto){
        List<BeanRecursoMaterial> recursos = new ArrayList<>();
        BeanRecursoMaterial recurso;
         try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from recursosMateriales where idProyecto=?");
            psm.setInt(1, idProyecto);
            rs = psm.executeQuery();
            while (rs.next()) {
             recurso = new BeanRecursoMaterial();
             recurso.setIdRecuroMat(rs.getInt("idRecursosMateriales"));
             recurso.setNombreRecursoMat(rs.getString("nombre"));
             recurso.setCostoUnitario(rs.getDouble("costoUnitario"));
             recurso.setCantidad(rs.getInt("cantidad"));
             recurso.setTotal(rs.getInt("total"));
             recurso.setIdProyecto(rs.getInt("idProyecto"));
             recursos.add(recurso);
            }
         }catch(SQLException ex){
             System.out.println("Error DaoRecursoMaterial listaRecursos" + ex);
         }finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoRecursoMaterial listaRecursos()cerrar" + ex);
            }
        }
        return recursos;
    }
/**
 * Método para registrar un recursos material
 * @param recurso la información del recurso a registrar
 * @param idProyecto el proyecto al que se le va a registrar el proyecto
 * @return regresa un boolean ya sea si se hizo o no el registro
 */
    public boolean registrarRecursoMaterial(BeanRecursoMaterial recurso, int idProyecto) {
        try {
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_registrarRecursoMaterial (?,?,?,?,?)}");
            csm.setString(1, recurso.getNombreRecursoMat());
            csm.setDouble(2, recurso.getCostoUnitario());
            csm.setInt(3, recurso.getCantidad());
            csm.setDouble(4, recurso.getTotal());
            csm.setInt(5, idProyecto);
            resultado = csm.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("Error DaoRecursoMaterial registrarRecursoMaterial()" + ex);
        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoRecursoMaterial registrarRecursoMaterial()cerrar" + ex);
            }
        }
        return resultado;
    }
/**
 * Método para ver si un recurso esta repetido 
 * @param recurso el recurso a consultar
 * @param idProyecto el id del proyecto donde se quiere saber si esta repetido
 * @return regresa el recurso material encontrado
 */
    public BeanRecursoMaterial consultarRecursoRepetido(BeanRecursoMaterial recurso, int idProyecto) {
        BeanRecursoMaterial recursoConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from recursosMateriales where nombre=? and idProyecto=?");
            psm.setString(1, recurso.getNombreRecursoMat());
            psm.setInt(2, idProyecto);
            rs = psm.executeQuery();
            if (rs.next()) {
                recursoConsultado = new BeanRecursoMaterial();
                recursoConsultado.setIdProyecto(rs.getInt("idRecursosMateriales"));
                recursoConsultado.setNombreRecursoMat(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoRecursoMaterial consultarRecursoRepetido()" + ex);

        } finally {
            try {
                con.close();
                psm.close();
                rs.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoRecursoMaterial consultarRecursoRepetido()cerrar" + ex);
            }
        }
        return recursoConsultado;
    }
    
    /**
     * Método para buscar si un recurso ya ha sido comprado
     * @param id el id del recursos material
     * @return regresa el material encontrado
     */
    public BeanRecursoMaterial buscarRecursoComprado(int id){

        BeanRecursoMaterial recurso = null;
         try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from recursosMateriales where idRecursosMateriales=?");
            psm.setInt(1, id);
            rs = psm.executeQuery();
            if (rs.next()) {
             recurso = new BeanRecursoMaterial();
             recurso.setIdRecuroMat(rs.getInt("idRecursosMateriales"));
             recurso.setNombreRecursoMat(rs.getString("nombre"));
             recurso.setCostoUnitario(rs.getDouble("costoUnitario"));
             recurso.setCantidad(rs.getInt("cantidad"));
             recurso.setTotal(rs.getInt("total"));
             recurso.setIdProyecto(rs.getInt("idProyecto"));
             
            }
         }catch(SQLException ex){
             System.out.println("Error DaoRecursoMaterial listaRecursos" + ex);
         }finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoRecursoMaterial listaRecursos()cerrar" + ex);
            }
        }
        return recurso;
    }
}
