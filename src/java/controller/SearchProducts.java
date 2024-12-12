package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Model;
import entity.Product;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        //get request json
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        Session session = HibernateUtil.getSessionFactory().openSession();

        //search all products
        Criteria criteria1 = session.createCriteria(Product.class);
        
//        System.out.println(requestJsonObject);

        //add category filter
        if (requestJsonObject.has("category_name")) {
            //category selected
            String categoryName = requestJsonObject.get("category_name").getAsString();

            //get category list from Db
            Criteria criteria2 = session.createCriteria(Category.class);
            criteria2.add(Restrictions.eq("name", categoryName));
            List<Category> categoryList = criteria2.list();

            //get models by category from DB
            Criteria criteria3 = session.createCriteria(Model.class);
            criteria3.add(Restrictions.in("category", categoryList));
            List<Model> modelList = criteria3.list();

            //filter products by model list from DB
            criteria1.add(Restrictions.in("model", modelList));
        }

        if (requestJsonObject.has("model_name")) {
            //model selected
            String modelName = requestJsonObject.get("model_name").getAsString();

            //get model list from Db
            Criteria criteria2 = session.createCriteria(Model.class);
            criteria2.add(Restrictions.eq("name", modelName));
            List<Model> modelList = criteria2.list();

            //get  category by models from DB
//            Criteria criteria3 = session.createCriteria(Category.class);
//            criteria3.add(Restrictions.in("model", modelList));
//            List<Category> categoryList = criteria3.list();

            //filter products by category list from DB
            criteria1.add(Restrictions.in("model", modelList));
        }

        
         if (requestJsonObject.has("search_text")) {           

             String searchText = requestJsonObject.get("search_text").getAsString();
             
            //filter products by search_text  from DB
            criteria1.add(Restrictions.like("title", searchText, MatchMode.ANYWHERE));
        }
        
        //filter products by sort from Db
        String sortText = requestJsonObject.get("sort_text").getAsString();

        if (sortText.equals("Short by Latest")) {
            criteria1.addOrder(Order.desc("id"));

        } else if (sortText.equals("Short by Oldest")) {
            criteria1.addOrder(Order.asc("id"));

        } else if (sortText.equals("Short by Name")) {
            criteria1.addOrder(Order.asc("title"));

        } else if (sortText.equals("Short by Price")) {
            criteria1.addOrder(Order.asc("price"));

        }

        //get all product count
        responseJsonObject.addProperty("allProductCount", criteria1.list().size());

        //set product range
        int firstResult = requestJsonObject.get("firstResult").getAsInt();
        criteria1.setFirstResult(firstResult);
        criteria1.setMaxResults(8);

        //get product list
        List<Product> productList = criteria1.list();
        System.out.println(productList.size());

        //get product list
        for (Product product : productList) {
            product.setUser(null);
        }

        responseJsonObject.addProperty("success", true);
        responseJsonObject.add("productList", gson.toJsonTree(productList));

        //send response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));

    }

}
