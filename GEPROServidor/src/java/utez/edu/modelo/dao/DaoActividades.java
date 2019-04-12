/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utez.edu.modelo.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import utez.edu.modelo.bean.BeanActividad;
import utez.edu.mx.utilerias.Conexion;


/**
 *
 * @author PC-MBD
 */
public class DaoActividades {
    Connection cn;
    ResultSet rs;
    PreparedStatement ps;
    
    private String sqlRegistroActividades=("{call pa_registrarActividades (?,?,?)}");
    
    public int registroActividad(BeanActividad actividad){
        int idNuevo=0;
        boolean registro=false;
        try {
            cn=Conexion.getConexion();
            ps=cn.prepareStatement(sqlRegistroActividades,PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1,actividad.getActividad());
            ps.setString(2,actividad.getDescripcion());
            ps.setInt(3, actividad.getIdUsuario());
            if (ps.executeUpdate()==1) {
                rs=ps.getGeneratedKeys();
                if (rs.next()) {
                    idNuevo=rs.getInt(1);
                    registro=true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al registrar una actividad"+e.getMessage());
        }finally{
            try {
                if (cn==null) {
                    cn.close();
                }
                if (ps==null) {
                    ps.close();
                }
                if (rs==null) {
                    rs.close();
                }
            } catch (Exception e) {
                System.out.println("Error al cerra conexion");
            }
        }
        
        return idNuevo;
    }
    public static void main(String[] args) {
  DaoActividades actividades = new DaoActividades();
  BeanActividad actvidad = new BeanActividad();
  actvidad.setActividad("Nombre Actividad");
  actvidad.setDescripcion("Descripcion de actividad");
  actvidad.setFechaActividad("2014-04-10");
  actvidad.setIdUsuario(1);
        System.out.println(actividades.registroActividad(actvidad));

    }    
}
