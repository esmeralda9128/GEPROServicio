package utez.edu.modelo.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import utez.edu.modelo.bean.BeanProyecto;
import utez.edu.modelo.bean.BeanUsuario;
import utez.edu.mx.utilerias.Conexion;

/**
 * Esta clase se utiliza para hacer métodos entre la aplicación y la base de
 * datos, referente a la tabla proyecto y otras tablas necesarias como usuario
 *
 * @author Esmeralda Estefanía Rodríguez Ramos
 * @version 1 15/03/2019
 *
 */
public class DaoProyecto {

    private ResultSet rs;
    private PreparedStatement psm;
    private Connection con;
    private CallableStatement csm;
    private boolean resultado;

    /**
     * Método para registrar un Proyecto
     *
     * @param beanProyecto es la información del proyecto
     * @param beanUsuario es la información del que sera el Líder del Proyecto
     * @return resultado regresa un dato de tipo boolean es decir si se hizo o
     * no el registro
     *
     *
     */
    public boolean registrarProyecto(BeanProyecto beanProyecto, BeanUsuario beanUsuario) {
        try {
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_registrarProyecto (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}");
            csm.setString(1, beanProyecto.getNombre());
            csm.setString(2, beanProyecto.getInicioProyecto());
            csm.setString(3, beanProyecto.getFinalProyecto());
            csm.setInt(4, beanProyecto.getSemanas());
            csm.setDouble(5, beanProyecto.getPresupuestoInicial());
            csm.setDouble(6, beanProyecto.getReserva());
            csm.setString(7, beanUsuario.getNombre());
            csm.setString(8, beanUsuario.getPrimerApellido());
            csm.setString(9, beanUsuario.getSegundoApellido());
            csm.setString(10, beanUsuario.getUsuario());
            csm.setString(11, beanUsuario.getPass());
            csm.setDouble(12, beanUsuario.getSalario());
            csm.setString(13, beanUsuario.getGradoEstudios());
            csm.setString(14, beanUsuario.getCarrera());
            csm.setString(15, beanUsuario.getRfc());
            csm.setString(16, beanUsuario.getEmail());
            resultado = csm.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto registroCuenta()" + ex);

        } finally {

            try {
                con.close();
                csm.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoProyecto registroCuenta()cerrar" + ex);
            }
        }
        return resultado;
    }

    /**
     * Método para verificar que no exista un Proyecto con nombre repetido
     *
     * @param beanProyecto es la infoación del proyecto a registrar, para saber
     * si no existe un proyecto con ese nombre
     * @return BeanProyecto regresa el proyecto entrado en caso de que se repita
     * el nombre
     *
     */
    public BeanProyecto verificarNombredeProyecto(BeanProyecto beanProyecto) {
        BeanProyecto proyectoConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from proyecto where nombre=?");
            psm.setString(1, beanProyecto.getNombre());
            rs = psm.executeQuery();
            while (rs.next()) {
                proyectoConsultado = new BeanProyecto();
                proyectoConsultado.setNombre(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto verificarNombredeProyecto()" + ex);
        } finally {
            try {
                con.close();
                psm.close();
                rs.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoProyecto verificarNombredeProyecto()cerrar" + ex);
            }
        }

        return proyectoConsultado;
    }

    /**
     * Método para verificar que no exista un Líder de proyecto ya registrado
     *
     * @param beanUsuario es la infoación del Líder de proyecto a registrar,
     * para saber si no existe un Líder de Proyecto con ese nombre
     * @return BeanUsuario regresa el usuario entrado de Líder de Proyecto en
     * caso de que se repita el nombre
     *
     */
    public BeanUsuario verificarNombredeLider(BeanUsuario beanUsuario) {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from usuario where nombre=? and primerApellido=? and segundoApellido=?");
            psm.setString(1, beanUsuario.getNombre());
            psm.setString(2, beanUsuario.getPrimerApellido());
            psm.setString(3, beanUsuario.getSegundoApellido());
            rs = psm.executeQuery();
            while (rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setNombre(rs.getString("nombre"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto verificarNombredeLider()" + ex);
        } finally {
            try {
                con.close();
                psm.close();
                rs.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoProyecto verificarNombredeLider()cerrar" + ex);
            }
        }

        return usuarioConsultado;
    }

    /**
     * Método para consultar todos lo proyecto registrados
     *
     * @return List de BeanProyectoregresa una listra de proyectos
     *
     */
    public List<BeanProyecto> consultarProyectos() {
        List<BeanProyecto> proyectos = new ArrayList<>();
        BeanProyecto proyecto = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from proyecto");
            rs = psm.executeQuery();
            while (rs.next()) {
                proyecto = new BeanProyecto();
                proyecto.setIdProyecto(rs.getInt("idProyecto"));
                proyecto.setNombre(rs.getString("nombre"));
                proyecto.setInicioProyecto(rs.getString("inicioProyecto"));
                proyecto.setFinalProyecto(rs.getString("finalProyecto"));
                proyecto.setSemanas(rs.getInt("semanas"));
                proyecto.setPresupuestoInicial(rs.getDouble("presupuestoInicial"));
                proyecto.setReserva(rs.getDouble("reserva"));
                proyecto.setPresupustoActual(rs.getDouble("presupuestoActual"));
                proyecto.setValorPlaneado(rs.getDouble("valorPlaneado"));
                DaoUsuario dao = new DaoUsuario();
                proyecto.setLider(dao.consultarLiderdeProyecto(proyecto.getIdProyecto()));
                proyectos.add(proyecto);
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto consultarProyectos()" + ex);
        } finally {
            try {
                con.close();
                psm.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoProyecto consultarProyectos()cerrar" + ex);
            }
        }

        return proyectos;
    }

    /**
     * Método para eliminar un Proyecto
     *
     * @param idProyecto se necesita para saber que Proyecto eliminar
     * @return resultado regresa un dato de tipo boolean es decir si se hizo o
     * no la eliminación
     *
     */
    public boolean eliminarProyecto(int idProyecto) {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_eliminarProyecto (?)}");
            csm.setInt(1, idProyecto);
            resultado = csm.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto eliminarProyecto()" + ex);

        } finally {
            try {
                con.close();
                csm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoProyecto eliminarProyecto()cerrar" + ex);
            }
        }

        return resultado;
    }

    /**
     * Método para buscar un proyecto por su id
     *
     * @param id es el id del proyecto que se quiere consultar
     * @return regresa el proyecto enconrado
     */
    public BeanProyecto consultarProyectoporId(int id) {
        BeanProyecto proyectoConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from proyecto where idProyecto=?");
            psm.setInt(1, id);
            rs = psm.executeQuery();
            while (rs.next()) {
                proyectoConsultado = new BeanProyecto();
                proyectoConsultado.setIdProyecto(rs.getInt("idProyecto"));
                proyectoConsultado.setNombre(rs.getString("nombre"));
                proyectoConsultado.setInicioProyecto(rs.getString("inicioProyecto"));
                proyectoConsultado.setFinalProyecto(rs.getString("finalProyecto"));
                proyectoConsultado.setSemanas(rs.getInt("semanas"));
                proyectoConsultado.setPresupuestoInicial(rs.getDouble("presupuestoInicial"));
                proyectoConsultado.setReserva(rs.getDouble("reserva"));
                proyectoConsultado.setValorPlaneado(rs.getDouble("valorPlaneado"));
                proyectoConsultado.setValorGanado(rs.getDouble("valorGanado"));
                proyectoConsultado.setPresupustoActual(rs.getDouble("presupuestoActual"));

            }
        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto consultarProyectoporId()" + ex);
        } finally {
            try {
                con.close();
                psm.close();
                rs.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoProyecto consultarProyectoporId()cerrar" + ex);
            }
        }

        return proyectoConsultado;
    }
    /**
     * Métado para calcular todo lo que se ha gastado del proyecto
     * @param id el id del proyecto a escatar información
     * @return regresa la cantidad gastada en el presuspuesto de recursos materiales y recursos humanos
     */

    public double consultarPresupuestoGastado(int id) {
        double presupuestoGastado = 0;
        try {
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_calcularCostoReal (?)}");
            csm.setInt(1, id);
            rs = csm.executeQuery();
            if (rs.next()) {
                presupuestoGastado = (rs.getDouble("CostoReal"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto consultarPresupuestoGastado()" + ex);
        } finally {
            try {
                con.close();
                csm.close();
                rs.close();
            } catch (SQLException e) {
                System.out.println("Error DaoProyecto consultarPresupuestoGastado()-ciere" + e);
            }
        }
        return presupuestoGastado;
    }

    /**
     * Método para ver la diferencia entre la fecha de inicio y la fecha actual
     * y saber cuantas semanas han pasado
     *
     * @param fecha se refiere a la fecha acutal
     * @return regresa los días de diferencia
     */
    public int consultarDias(String fecha) {
        int diasDiferencia = 0;
        try {
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_consultarDiferenciaDias (?)}");
            csm.setString(1, fecha);
            rs = csm.executeQuery();
            while (rs.next()) {
                diasDiferencia = rs.getInt("dias");
            }
        } catch (SQLException ex) {
            System.out.println("Error en DaoProyecto consultarDias()" + ex);
        } finally {
            try {
                con.close();
                csm.close();
                rs.close();
            } catch (SQLException ex) {
                System.out.println("Error en DaoProyecto consultarDias()-cierre" + ex);
            }
        }

        return diasDiferencia;
    }

    /**
     * *
     * Método para contrar cuantos recursos humanos estan involucrados en un
     * determinado proyecto
     *
     * @param idProyecto es el id del proyecto del que nos interesa saber sus
     * recursos
     * @return un entero que es el número de cuantos recursos humanos estan
     * involucrados
     */
    public int contarRecursosHumanos(int idProyecto) {
        int suma = 0;
        try {
            con = Conexion.getConexion();
            psm = con.prepareCall("select COUNT(idUsuario)as RecursoHumano from usuario where idProyecto=? and tipo=3");
            psm.setInt(1, suma);
            rs = psm.executeQuery();
            if (rs.next()) {
                suma = rs.getInt("RecursoHumano");
            }

        } catch (SQLException ex) {
            System.out.println("Error DaoProyecto contarRecursosHumanos()" + ex);
        } finally {
            try {
                con.close();
                psm.close();
                rs.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoProyecto contarRecursosHumanos()-cierre" + ex);
            }
        }
        return suma;
    }

    /**
     * Método para asignar el valor ganado a un proyecto
     *
     * @param idProyecto el id del proyecto donde se va a actualizar el valor
     * agregado
     * @return regresa un booleano si se pudo hacer o no
     */
    public boolean agregarValorGanado(int idProyecto, double valorGanado) {
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("update proyecto set valorGanado=? where idProyecto=?");
            psm.setDouble(1, valorGanado);
            psm.setInt(2, idProyecto);
            resultado = psm.executeUpdate() > 0;
        } catch (SQLException ex) {
            System.out.println("Error en DaoProyecto agregarValorGanado()" + ex);
        } finally {
            try {
                con.close();
                psm.close();
            } catch (SQLException ex) {
                System.out.println("Error en DaoProyecto agregarValorGanado()-cierre" + ex);
            }
        }

        return resultado;
    }

    public static void main(String[] args) {
        DaoProyecto daoProyecto = new DaoProyecto();
        System.out.println(daoProyecto.agregarValorGanado(1, 26));

    }

}
