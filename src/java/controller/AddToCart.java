package controller;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        Gson gson = new Gson();
        Response_DTO response_DTO = new Response_DTO();

        try {
            String id = request.getParameter("id");
            String qty = request.getParameter("qty");

            if (!Validation.isInteger(id)) {
                response_DTO.setContent("Product Not Found");
            } else if (!Validation.isInteger(qty)) {
                response_DTO.setContent("Invalid Quantity");
            } else {

                int productId = Integer.parseInt(id);
                int productQty = Integer.parseInt(qty);

                if (productQty <= 0) {
                    response_DTO.setContent("Quantity Must be Greater than 0");

                } else {

                    Product product = (Product) session.get(Product.class, productId);

                    if (product != null) {
                        response_DTO.setContent("Product Found");

                        if (request.getSession().getAttribute("user") != null) {
                            //DB C

                            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");

                            //Search U
                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                            User user = (User) criteria1.uniqueResult();

                            //Check DB C
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            criteria2.add(Restrictions.eq("product", product));

                            if (criteria2.list().isEmpty()) {
//                                response_DTO.setContent("Item Not Found in Cart");

                                if (productQty <= product.getQty()) {
                                    //Add Product Into Cart

                                    Cart cart = new Cart();
                                    cart.setProduct(product);
                                    cart.setQty(productQty);
                                    cart.setUser(user);
                                    session.save(cart);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product Added To The Cart");
                                } else {
                                    response_DTO.setContent("Quantity Not Available");
                                }

                            } else {
//                                    response_DTO.setContent("Quantity Not Available");
                                Cart cartItem = (Cart) criteria2.uniqueResult();

                                if ((cartItem.getQty() + productQty) <= product.getQty()) {

                                    cartItem.setQty(cartItem.getQty() + productQty);
                                    session.update(cartItem);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product Added To The Cart");

                                } else {
                                    response_DTO.setContent("Can't Update Your Cart. Quantity Not Available");

                                }
                            }

                        } else {
                            //Session Cart

                            HttpSession httpSession = request.getSession();

                            if (httpSession.getAttribute("sessionCart") != null) {
//                                Session Cart Found
                                ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) httpSession.getAttribute("sessionCart");

                                Cart_DTO foundCart_DTO = null;

                                for (Cart_DTO cart_DTO : sessionCart) {
                                    if (cart_DTO.getProduct().getId() == product.getId()) {
                                        foundCart_DTO = cart_DTO;
                                        break;
                                    }
                                }
                                if (foundCart_DTO != null) {
//                                  Product Found

                                    if ((foundCart_DTO.getQty() + productQty) <= product.getQty()) {
//                                        Update Qty
                                        foundCart_DTO.setQty(foundCart_DTO.getQty() + productQty);

                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Cart Item Updated");

                                    } else {
//                                        Quantity Not Available
                                        response_DTO.setContent("Quantity Not Available");

                                    }
                                } else {
//                                  Product Not Found

                                    if (productQty <= product.getQty()) {
//                                        Add To Session Cart

                                        Cart_DTO cart_DTO = new Cart_DTO();
                                        cart_DTO.setProduct(product);
                                        cart_DTO.setQty(productQty);
                                        sessionCart.add(cart_DTO);

                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Product Added To The Cart");

                                    } else {
//                                        Quantity Not Available
                                        response_DTO.setContent("Quantity Not Available");

                                    }
                                }

                            } else {
//                                Session Cart Not Found
                                if (productQty <= product.getQty()) {
//                                    Add To Session Cart

                                    ArrayList<Cart_DTO> sessionCart = new ArrayList<>();

                                    Cart_DTO cart_DTO = new Cart_DTO();
                                    cart_DTO.setProduct(product);
                                    cart_DTO.setQty(productQty);
                                    sessionCart.add(cart_DTO);

                                    httpSession.setAttribute("sessionCart", sessionCart);

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product Added To The Cart");

                                } else {
//                                    Quantity Not Available
                                    response_DTO.setContent("Quantity Not Available");

                                }
                            }

                        }

                    } else {
                        response_DTO.setContent("Product Not Found");

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            response_DTO.setContent("Unable To Process Your Request. Please try Again");

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));
        session.close();

    }
}
