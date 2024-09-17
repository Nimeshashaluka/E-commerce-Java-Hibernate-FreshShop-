package controller;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.Product_DTO;
import dto.Response_DTO;
import entity.Model;
import entity.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadProduct", urlPatterns = {"/LoadProduct"})
public class LoadProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

//        System.out.println("meka hari");
        Gson gson = new Gson();
        HttpSession httpSession = request.getSession();
        ArrayList<Product_DTO> product_DTO_List = new ArrayList<>();

        Session session = HibernateUtil.getSessionFactory().openSession();

        try {
            Criteria criteria1 = session.createCriteria(Product.class);
            List<Product> productList = criteria1.list();

            for (Product product : productList) {
                Product_DTO product_DTO = new Product_DTO();

                product_DTO.setProduct(product);
                product_DTO_List.add(product_DTO);

            }

//            Criteria criteria2 =session.createCriteria(Product.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(product_DTO_List));
        session.close();

    }

}
