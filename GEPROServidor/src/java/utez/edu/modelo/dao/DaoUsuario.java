package utez.edu.modelo.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import utez.edu.modelo.bean.BeanActividad;
import utez.edu.modelo.bean.BeanUsuario;
import utez.edu.mx.utilerias.Conexion;

/**
 * Esta clase se utiliza para hacer métodos entre la aplicación y la base de
 * datos, referente a la tabla usuario
 *
 * @author Esmeralda Estefania Rodríguez Ramos
 * @version 1 15/03/2019
 *
 */
public class DaoUsuario {

    private ResultSet rs;
    private PreparedStatement psm;
    private Connection con;
    private CallableStatement csm;
    private boolean resultado;

    /**
     * Método para consultar el Líder un Proyecto
     *
     * @param idProyecto, es el id para saber a que proyecto pertenece
     * @return BeanUsuario regresa si encuentra un Líder de Proyecto que concida
     * con ese proyecto
     *
     *
     */
    public BeanUsuario consultarLiderdeProyecto(int idProyecto) {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from usuario where idProyecto=? and tipo=2");
            psm.setInt(1, idProyecto);
            rs = psm.executeQuery();
            if (rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setId(rs.getInt("idUsuario"));
                usuarioConsultado.setNombre(rs.getString("nombre"));
                System.out.println("Nombre " + usuarioConsultado.getNombre());
                usuarioConsultado.setPrimerApellido(rs.getString("primerApellido"));
                usuarioConsultado.setSegundoApellido(rs.getString("segundoApellido"));
                usuarioConsultado.setUsuario(rs.getString("usuario"));
                usuarioConsultado.setPass(rs.getString("pass"));
                usuarioConsultado.setRol(rs.getString("rol"));
                usuarioConsultado.setSalario(rs.getDouble("salario"));
                usuarioConsultado.setGradoEstudios(rs.getString("gradoEstudios"));
                usuarioConsultado.setCarrera(rs.getString("carrera"));
                usuarioConsultado.setRfc(rs.getString("rfc"));
                usuarioConsultado.setEmail(rs.getString("email"));
                usuarioConsultado.setTipo(rs.getInt("tipo"));
                usuarioConsultado.setIdProyecto(rs.getInt("idProyecto"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario verificarNombredeLider()" + ex);

        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario verificarNombredeLider()cerrar" + ex);
            }
        }

        return usuarioConsultado;
    }

    /**
     *
     * Método para consultar un usuario cuando inicia sesión
     *
     * @param usuario el username del usuario
     * @param pass la contraseña de usuario
     * @return BeanUsuario regresa el usuario encontrado
     */
    public BeanUsuario consultarUsuario(String usuario, String pass) {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from usuario where usuario=? and pass=?");
            psm.setString(1, usuario);
            psm.setString(2, pass);
            rs = psm.executeQuery();
            if (rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setId(rs.getInt("idUsuario"));
                usuarioConsultado.setNombre(rs.getString("nombre"));
                usuarioConsultado.setPrimerApellido(rs.getString("primerApellido"));
                usuarioConsultado.setSegundoApellido(rs.getString("segundoApellido"));
                usuarioConsultado.setUsuario(rs.getString("usuario"));
                usuarioConsultado.setPass(rs.getString("pass"));
                usuarioConsultado.setRol(rs.getString("rol").replace(" ", ""));
                usuarioConsultado.setSalario(rs.getDouble("salario"));
                usuarioConsultado.setGradoEstudios(rs.getString("gradoEstudios"));
                usuarioConsultado.setCarrera(rs.getString("carrera"));
                usuarioConsultado.setRfc(rs.getString("rfc"));
                usuarioConsultado.setEmail(rs.getString("email"));
                usuarioConsultado.setTipo(rs.getInt("tipo"));
                usuarioConsultado.setIdProyecto(rs.getInt("idProyecto"));
            } else {
                usuarioConsultado = consultarAdministrador(usuario, pass);
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario verificarNombredeLider()" + ex);

        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario verificarNombredeLider()cerrar" + ex);
            }
        }

        return usuarioConsultado;
    }

    /**
     * Método para consultar un usuario del tipo Administrador para el incio de
     * sesión Este esta bien
     *
     * @param usuario el username del Administrador
     * @param pass la contraseña del Administrador
     * @return retorna un BeanUsuario que coincida con los dos parametros
     */
    public BeanUsuario consultarAdministrador(String usuario, String pass) {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from administrador where usuario=? and pass=?");
            psm.setString(1, usuario);
            psm.setString(2, pass);
            rs = psm.executeQuery();
            if (rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setId(rs.getInt("idAdministrador"));
                usuarioConsultado.setNombre(rs.getString("nombre"));
                usuarioConsultado.setUsuario(rs.getString("usuario"));
                usuarioConsultado.setRol("administrador");
                usuarioConsultado.setPass(rs.getString("pass"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario verificarNombredeLider()" + ex);

        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario verificarNombredeLider()cerrar" + ex);
            }
        }
        return usuarioConsultado;
    }

    /**
     * Método para consultar la información personal del Admistrador
     *
     * @return BeanUsuario con toda la información del Administrador
     */
    public BeanUsuario consultarPerfilAdministrador() {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from administrador");
            rs = psm.executeQuery();
            if (rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setNombre(rs.getString("nombre"));
                usuarioConsultado.setUsuario(rs.getString("usuario"));
                usuarioConsultado.setPass(rs.getString("pass"));
                usuarioConsultado.setCarrera(rs.getString("carrera"));
                usuarioConsultado.setGradoEstudios(rs.getString("gradoEstudios"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario verificarNombredeLider()" + ex);

        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario verificarNombredeLider()cerrar" + ex);
            }
        }
        return usuarioConsultado;
    }

    /**
     * Método para modificar la información personal del Administrador Esta bien
     *
     * @param administrador es la información a modificar del Administrador
     * @return retornar un booleano es decir si se hizo la actulización o no
     */
    public boolean modificarPerfilAdministrador(BeanUsuario administrador) {
        try {
            con = Conexion.getConexion();
            psm = con.prepareCall("update administrador set nombre=?,pass=?,usuario=?,carrera=?,gradoEstudios=?");
            psm.setString(1, administrador.getNombre());
            psm.setString(2, administrador.getPass());
            psm.setString(3, administrador.getUsuario());
            psm.setString(4, administrador.getCarrera());
            psm.setString(5, administrador.getGradoEstudios());

            resultado = psm.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario en modificarPerfilAdministrador" + ex);
        } finally {
            try {
                con.close();
                psm.close();
            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario en conexion modificarPerfilAdministrador-cierre" + ex);
            }
        }
        return resultado;
    }

    /**
     *Método para consultar el perfil de un líder de proyecto
     * @param usuario el usuario del líder de proyecto
     * @param idProyecto es el id del proyecto al que pertenece
     * @return regresa el usuario encontrado
     */
    public BeanUsuario consultarPerfildeUsuario(String usuario, int idProyecto) {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from usuario where usuario=? and idProyecto=?");
            psm.setString(1, usuario);
            psm.setInt(2, idProyecto);
            rs = psm.executeQuery();
            if (rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setId(rs.getInt("idUsuario"));
                usuarioConsultado.setNombre(rs.getString("nombre"));
                usuarioConsultado.setPrimerApellido(rs.getString("primerApellido"));
                usuarioConsultado.setSegundoApellido(rs.getString("segundoApellido"));
                usuarioConsultado.setUsuario(rs.getString("usuario"));
                usuarioConsultado.setPass(rs.getString("pass"));
                usuarioConsultado.setRol(rs.getString("rol"));
                usuarioConsultado.setSalario(rs.getDouble("salario"));
                usuarioConsultado.setGradoEstudios(rs.getString("carrera"));
                usuarioConsultado.setRfc(rs.getString("rfc"));
                usuarioConsultado.setEmail(rs.getString("email"));
                usuarioConsultado.setTipo(rs.getInt("tipo"));
                usuarioConsultado.setIdProyecto(rs.getInt("idProyecto"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario consultarPerfildeUsuario()" + ex);

        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {

                System.out.println("Error DaoUsuario consultarPerfildeUsuario()cerrar" + ex);

            }
        }
        return usuarioConsultado;
    }

    /**
     * Método para regresa todos los recurso humanos de un proyecto
     * @param idProyecto el id del proyecto en cuestión
     * @return regresa la lista de los recursos humanos
     */
    public List<BeanUsuario> consultarRescursos(int idProyecto) {
        List<BeanUsuario> recursos = new ArrayList();
        BeanUsuario recurso;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from usuario where tipo=3 and idProyecto=?");
            psm.setInt(1, idProyecto);
            rs = psm.executeQuery();
            while (rs.next()) {
                recurso = new BeanUsuario();
                recurso.setId(rs.getInt("idUsuario"));
                recurso.setNombre(rs.getString("nombre"));
                recurso.setPrimerApellido(rs.getString("primerApellido"));
                recurso.setSegundoApellido(rs.getString("segundoApellido"));
                recurso.setUsuario(rs.getString("usuario"));
                recurso.setPass(rs.getString("pass"));
                recurso.setRol(rs.getString("rol"));
                recurso.setSalario(rs.getDouble("salario"));
                recurso.setGradoEstudios(rs.getString("gradoEstudios"));
                recurso.setCarrera(rs.getString("carrera"));
                recurso.setRfc(rs.getString("rfc"));
                recurso.setEmail(rs.getString("email"));
                recurso.setIdProyecto(rs.getInt("idProyecto"));
                recursos.add(recurso);
            }
        } catch (SQLException e) {

        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario consultarRescursos()cerrar" + ex);
            }
        }
        return recursos;
    }


    /**
     * Método para insertar un nuevo recurso humano
     *
     * @param beanUsuario la información del recurso humano
     * @param idProyecto id del proyecto al que se le va a ingresar el recurso
     * @return regresa un booleao de si se registro el recurso o no
     */
    public boolean registrarRecursoHumano(BeanUsuario beanUsuario, int idProyecto) {
        try {
            con = Conexion.getConexion();
            csm = con.prepareCall("{call dbo.pa_registrarRecursoHumano (?,?,?,?,?,?,?,?,?,?,?,?)}");
            csm.setString(1, beanUsuario.getNombre());
            csm.setString(2, beanUsuario.getPrimerApellido());
            csm.setString(3, beanUsuario.getSegundoApellido());
            csm.setString(4, beanUsuario.getUsuario());
            csm.setString(5, beanUsuario.getPass());
            csm.setDouble(6, beanUsuario.getSalario());
            csm.setString(7, beanUsuario.getGradoEstudios());
            csm.setString(8, beanUsuario.getCarrera());
            csm.setString(9, beanUsuario.getRfc());
            csm.setString(10, beanUsuario.getEmail());
            csm.setString(11, beanUsuario.getRol());
            csm.setInt(12, idProyecto);
            resultado = csm.executeUpdate() == 1;
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario registrarRecursoHumano()" + ex);

        }
        return resultado;
    }

    /**
     *Método para saber si ya se ha registrado el recurso en otro proyecto
     * @param beanUsuario el información del recurso humano a consultar
     * @return regresa el recurso humano encontrado
     */
    public BeanUsuario consultarUsuariosRepetidos(BeanUsuario beanUsuario) {
        BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from usuario where nombre=? and primerApellido=? and segundoApellido=?");
            psm.setString(1, beanUsuario.getNombre());
            psm.setString(2, beanUsuario.getPrimerApellido());
            psm.setString(3, beanUsuario.getSegundoApellido());
            rs = psm.executeQuery();
            if (rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setId(rs.getInt("idUsuario"));
                usuarioConsultado.setNombre(rs.getString("nombre"));
                usuarioConsultado.setPrimerApellido(rs.getString("primerApellido"));
                usuarioConsultado.setSegundoApellido(rs.getString("segundoApellido"));
                usuarioConsultado.setUsuario(rs.getString("usuario"));
                usuarioConsultado.setPass(rs.getString("pass"));
                usuarioConsultado.setRol(rs.getString("rol"));
                usuarioConsultado.setSalario(rs.getDouble("salario"));
                usuarioConsultado.setGradoEstudios(rs.getString("carrera"));
                usuarioConsultado.setRfc(rs.getString("rfc"));
                usuarioConsultado.setEmail(rs.getString("email"));
                usuarioConsultado.setTipo(rs.getInt("tipo"));
                usuarioConsultado.setIdProyecto(rs.getInt("idProyecto"));
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario consultarUsuariosRepetidos()" + ex);

        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario consultarUsuariosRepetidos()cerrar" + ex);
            }
        }

        return usuarioConsultado;
    }
  /***
   * Método para ver todos los recursos humanos de un proyecto
   * @param idProyecto el id del proyecto del que deseamos la información
   * @return regresa la lista de recursos
   */
   public List<BeanUsuario> consultarUsuarios3(int idProyecto){
       List<BeanUsuario> usuarios = new ArrayList<>();
       BeanUsuario usuarioConsultado = null;
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from usuario where idProyecto=? and tipo=3");
            psm.setInt(1, idProyecto);
            rs = psm.executeQuery();
            while(rs.next()) {
                usuarioConsultado = new BeanUsuario();
                usuarioConsultado.setId(rs.getInt("idUsuario"));
                usuarioConsultado.setNombre(rs.getString("nombre"));
                usuarioConsultado.setPrimerApellido(rs.getString("primerApellido"));
                usuarioConsultado.setSegundoApellido(rs.getString("segundoApellido"));
                usuarioConsultado.setUsuario(rs.getString("usuario"));
                usuarioConsultado.setPass(rs.getString("pass"));
                usuarioConsultado.setRol(rs.getString("rol"));
                usuarioConsultado.setSalario(rs.getDouble("salario"));
                usuarioConsultado.setGradoEstudios(rs.getString("gradoEstudios"));
                usuarioConsultado.setCarrera(rs.getString("carrera"));
                usuarioConsultado.setRfc(rs.getString("rfc"));
                usuarioConsultado.setEmail(rs.getString("email"));
                usuarioConsultado.setTipo(rs.getInt("tipo"));
                usuarioConsultado.setIdProyecto(rs.getInt("idProyecto"));
                usuarios.add(usuarioConsultado);
            }
        }catch (SQLException ex) {
            System.out.println("Error DaoUsuario consultarUsuarios3()" + ex);
        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario consultarUsuarios3() cerrar" + ex);
            }
        }
       return usuarios;
   }
   
   public List<BeanActividad> mostrarActividades(int idUsuario){
       List<BeanActividad> misActividades = new ArrayList<>();
       BeanActividad actividad = null;
       
        try {
            con = Conexion.getConexion();
            psm = con.prepareStatement("select * from actividades where idUsuario = ?");
            psm.setInt(1, idUsuario);
            rs = psm.executeQuery();
            while (rs.next()) {                
                actividad = new BeanActividad();
                actividad.setIdActividad(rs.getInt("idActividades"));
                actividad.setActividad(rs.getString("nombreActividad"));
                actividad.setDescripcion(rs.getString("descripcion"));
                actividad.setFechaActividad(rs.getString("fecha"));
                misActividades.add(actividad);
            }
        } catch (SQLException ex) {
            System.out.println("Error DaoUsuario mostrarActividades()" + ex);
        } finally {
            try {
                con.close();
                psm.close();

            } catch (SQLException ex) {
                System.out.println("Error DaoUsuario mostrarActividades() cerrar" + ex);
            }
        }
        return misActividades;
   }
   private String sqlConsultaLogin=("select * from usuario where usuario=? and pass=?");
    public BeanUsuario cosultaLogin(String usuario,String pass){
        BeanUsuario user= new BeanUsuario();
        try {
            con=Conexion.getConexion();
            psm=con.prepareStatement(sqlConsultaLogin);
            psm.setString(1, usuario);
            psm.setString(2,pass);
            rs=psm.executeQuery();
            while (rs.next()) {                
                user.setId(rs.getInt("idUsuario"));
                user.setNombre(rs.getString("nombre"));
                user.setPrimerApellido(rs.getString("primerApellido"));
                user.setSegundoApellido(rs.getString("segundoApellido"));
                user.setUsuario(rs.getString("usuario"));
                user.setPass(rs.getString("pass"));
                user.setRol(rs.getString("rol"));
                user.setSalario(rs.getDouble("salario"));
                user.setGradoEstudios(rs.getString("gradoEstudios"));
                user.setCarrera(rs.getString("carrera"));
                user.setRfc(rs.getString("rfc"));
                user.setEmail(rs.getString("email"));
                user.setIdProyecto(rs.getInt("idProyecto"));
                user.setTipo(rs.getInt("tipo"));
                System.out.println("Dao Usuario ->"+user.toString());
            }
        } catch (Exception e) {
            System.out.println("Error en la consulta del usuario ->"+e.getMessage());
        }finally{
            try {
                if (con==null) {
                    con.close();
                }
                if (rs==null) {
                    rs.close();
                }
                if (psm==null) {
                    psm.close();
                }
            } catch (Exception e) {
                System.out.println("Error al cerrar la conexion ->"+e.getMessage());
            }
        }   
        return user;
    }

}
