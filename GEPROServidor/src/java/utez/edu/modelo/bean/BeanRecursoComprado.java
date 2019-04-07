/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utez.edu.modelo.bean;

/**
 *
 * @author Alberto
 */
public class BeanRecursoComprado {
    private int idRecursoCom, semana;
    private String fecha;
    private int idProyecto;
    private BeanRecursoMaterial materiales;

    public BeanRecursoComprado(int idRecursoCom, String fecha, BeanRecursoMaterial materiales) {
        this.idRecursoCom = idRecursoCom;
        this.fecha = fecha;
        this.materiales = materiales;
    }

    public int getSemana() {
        return semana;
    }

    public void setSemana(int semana) {
        this.semana = semana;
    }

    public BeanRecursoComprado() {
    }

    public int getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(int idProyecto) {
        this.idProyecto = idProyecto;
    }

    public int getIdRecursoCom() {
        return idRecursoCom;
    }

    public void setIdRecursoCom(int idRecursoCom) {
        this.idRecursoCom = idRecursoCom;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public BeanRecursoMaterial getMateriales() {
        return materiales;
    }

    public void setMateriales(BeanRecursoMaterial materiales) {
        this.materiales = materiales;
    }
    
}
