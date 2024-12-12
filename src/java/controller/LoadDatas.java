/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Model;
import entity.Product;
import entity.productitemsize;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

/**
 *
 * @author nimes
 */
@WebServlet(name = "LoadDatas", urlPatterns = {"/LoadDatas"})
public class LoadDatas extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
        System.out.println("okser");
    
        Gson gson = new Gson();
        JsonObject jsonObject = new JsonObject();

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria1 = session.createCriteria(Category.class);
        criteria1.addOrder(Order.asc("name"));
        List<Category> categoryList = criteria1.list();

        Criteria criteria2 = session.createCriteria(Model.class);
        criteria2.addOrder(Order.asc("name"));
        List<Model> modelList = criteria2.list();
        
        Criteria criteria5 = session.createCriteria(Product.class);
        criteria5.addOrder(Order.desc("id"));
        jsonObject.addProperty("allProductCount", criteria5.list().size());
        
        criteria5.setFirstResult(1);
        criteria5.setMaxResults(8);
        
        List<Product>productList =criteria5.list();
        
        for(Product product : productList){
            product.setUser(null);
        }
        
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("modelList", gson.toJsonTree(modelList));
        jsonObject.add("productList", gson.toJsonTree(productList));
        jsonObject.addProperty("success", true);

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
       session.close();
    }

}
