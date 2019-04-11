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

    /**
     * Método para consultar una nómina de un usuario determinado en la semana
     * actual
     *
     * @param semana semana actual
     * @param idUsuario del usuario a consultar
     * @return regresa si encontró una nómina de ese usuario
     */
    public BeanNomina consultarNomina(int semana, int idUsuario) {
        BeanNomina beanNomina = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareCall("select * from nomina where idUsuario=? and semana=?");
            psm.setInt(1, idUsuario);
            psm.setInt(2, semana);
            rs = psm.executeQuery();
            if (rs.next()) {
                beanNomina = new BeanNomina();
                beanNomina.setIdNomina(rs.getInt("idNomina"));
                beanNomina.setFecha(rs.getString("fecha"));
                beanNomina.setIdProyecto(rs.getInt("idProyecto"));
                beanNomina.setIdUsuario(rs.getInt("idUsuario"));
                beanNomina.setSemana(rs.getInt("semana"));
                beanNomina.setPagado(rs.getInt("pagado"));
                beanNomina.setValorGanado(rs.getDouble("valorGanado"));
            }
        } catch (SQLException ex) {
            System.out.println("Erroe en DaoNomina consultarNomina()");
        } finally {
            try {
                con.close();
                psm.close();
                rs.close();
            } catch (SQLException e) {
                System.out.println("Erroe en DaoNomina consultarNomina()-cierre");
            }
        }
        return beanNomina;
    }

    /**
     * *
     * Métod para pagar la nómina de un usuario
     *
     * @param idUsuario se necesita saber a que usuario se le va a pagar
     * @param idProyecto para saber a que proyecto pertenece el usuario
     * @param fecha la fecha en la que se esta haciendo el pago de nómina
     * @param semana la semana a la que corresponde la fecha dentro del proyecto
     * @param valor el valor ganado de cada empleado
     * @return regresa un booleano de s se pudo o no hacer el pago
     */
    public boolean pagarNomina(int idUsuario, int idProyecto, String fecha, int semana, double valor) {
        try {
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_pagarNomina (?,?,?,?,?)}");
            csm.setInt(1, idUsuario);
            csm.setInt(2, idProyecto);
            csm.setString(3, fecha);
            csm.setInt(4, semana);
            csm.setDouble(5, valor);
            resultado = csm.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Error DaoNomina pagarNomina()" + e);
        } finally {
            try {
                con.close();
                csm.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoNomina pagarNomina()-cierre" + ex);
            }
        }
        return resultado;
    }

    /**
     * *
     * Método para saber la suma de todo el valor ganado de un proyecto en
     * determinada semana
     *
     * @param idProyecto El id del proyecto del qie se desea saber la
     * información
     * @param semana La semana de la se necesita la información
     * @return regresa la suma del valor ganado de esa semana del proyecto
     * determinado
     */
    public double sumaValorGanado(int idProyecto, int semana) {
        double suma = 0;
        try {
            con = Conexion.getConexion();
            psm = con.prepareCall("select sum(valorGanado)as Ganado from nomina where idProyecto=? and semana=?");
            psm.setInt(1, idProyecto);
            psm.setInt(2, semana);
            rs = psm.executeQuery();
            if (rs.next()) {
                suma = rs.getDouble("Ganado");
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoNomina sumaValorGanado" + ex);
        } finally {
            try {
                con.close();
                psm.close();
                rs.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoNomina sumaValorGanado -cierre" + ex);
            }
        }
        return suma;
    }

    public static void main(String[] args) {
        DaoNomina daoNomina = new DaoNomina();
        System.out.println(daoNomina.pagarNomina(6, 1, "2019-04-10", 0, 10));

    }

}
