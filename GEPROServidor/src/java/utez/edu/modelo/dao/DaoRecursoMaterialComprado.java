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
 * @author Esmeralda Estefanía Rodríguez Ramos
 */
public class DaoRecursoMaterialComprado {
      private ResultSet rs;
    private PreparedStatement psm;
    private Connection con;
    private CallableStatement csm;
    private boolean resultado;
    
    /**
     * Método para buscar todos los recursos materiales que se han comprado de un proyecto de esa semana
     * @param idProyecto es el proyecto de que se quiere saber la información
     * @param semana semana de la que nos interesa saber los recuros comprados
     * @return Lista los recursos del proyecto en cuestión
     */
    public List<BeanRecursoComprado> buscarRecursosComprado(int idProyecto, int semana){
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
    
    /**
     * Método para buscar buscar si un recurso se compro en determinada semana
     * @param idRecurso
     * @param semana
     * @return 
     */
    public BeanRecursoComprado buscarRecursoComprado(int idRecurso, int semana){
       BeanRecursoComprado recurso =null;
       
        DaoRecursoMaterial daoRecursoMaterial = new DaoRecursoMaterial();
         try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from recursoComprado where idRecursosMateriales=? and semana=?");
            psm.setInt(1, idRecurso);
            psm.setInt(2, semana);
            rs = psm.executeQuery();
            if (rs.next()) {
             recurso = new BeanRecursoComprado();
             recurso.setIdRecursoCom(rs.getInt("idRecursoComprado"));
             recurso.setFecha(rs.getString("fecha"));
             recurso.setIdProyecto(rs.getInt("idProyecto"));
             BeanRecursoMaterial recursoMaterial = daoRecursoMaterial.buscarRecursoComprado(rs.getInt("idRecursosMateriales"));
             recurso.setMateriales(recursoMaterial);
             
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
        return recurso;
    }
    
    public boolean comprarRecursoMaterial(int idProyecto, int idMateral,String fecha,int semana){
        try{
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_comprarMaterial (?,?,?,?)}");
            csm.setInt(1, idProyecto);
            csm.setInt(2, idMateral);
            csm.setString(3, fecha);
            csm.setInt(4, semana);
            resultado = csm.executeUpdate()==1;
        }catch(SQLException ex){
            System.out.println("Error DaoRecursoMaterialComprado comprarRecursoMaterial()");
        }finally{
            try{
                con.close();
                csm.close();
            }catch(SQLException ex){
            System.out.println("Error DaoRecursoMaterialComprado comprarRecursoMaterial()-cierre");
            }        
        }
        return resultado;
    }
    
 
}
