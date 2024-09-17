package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.Order_Item;
import entity.Order_Status;
import entity.Orders;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.PayHere;
import model.Validation;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Pcheckout", urlPatterns = {"/Pcheckout"})
public class Pcheckout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        HttpSession httpSession = request.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        boolean currentAddressCheked = requestJsonObject.get("currentAddressCheked").getAsBoolean();
        String first_name = requestJsonObject.get("first_name").getAsString();
        String last_name = requestJsonObject.get("last_name").getAsString();
        String address1 = requestJsonObject.get("address1").getAsString();
        String address2 = requestJsonObject.get("address2").getAsString();
        String postal_Code = requestJsonObject.get("postal_Code").getAsString();
        String mobile = requestJsonObject.get("mobile").getAsString();
        String city_id = requestJsonObject.get("city_id").getAsString();

//        System.out.println(currentAddressCheked);      
//        System.out.println(first_name);
//        System.out.println(last_name);
//        System.out.println(address1);
//        System.out.println(address2);
//        System.out.println(postal_Code);     
//        System.out.println(mobile);
//        System.out.println(city_id);
        if (httpSession.getAttribute("user") != null) {
            //search user
            User_DTO user_DTO = (User_DTO) httpSession.getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (currentAddressCheked) {
                //current address

                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                if (criteria2.list().isEmpty()) {
                    responseJsonObject.addProperty("message", "Current Address Not Found. Please Create a New Address");
                } else {
                    //get current address ek
                    Address address = (Address) criteria2.list().get(0);

                    saveOrder(session, transaction, user, address, responseJsonObject);
                }
            } else {
                //new address

                if (first_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please Fill First Name");
                } else if (last_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please Fill Last Name");
                } else if (!Validation.isInteger(city_id)) {
                    responseJsonObject.addProperty("message", "Invalid City Selected");
                } else {
                    //Check city
                    Criteria criteria3 = session.createCriteria(City.class);
                    criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {
                        responseJsonObject.addProperty("message", "Invalid City Selected");
                    } else {
                        City city = (City) criteria3.list().get(0);

                        if (address1.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please Fill Address Line 1");
                        } else if (address2.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please Fill Address Line 2");
                        } else if (postal_Code.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please Fill Postal Code");
                        } else if (postal_Code.length() != 5) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");
                        } else if (!Validation.isInteger(postal_Code)) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");
                        } else if (mobile.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please Fill Mobile Number");
                        } else if (!Validation.isMobile(mobile)) {
                            responseJsonObject.addProperty("message", "Invalid Mobile Number");
                        } else {
                            //add new Address

                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(address1);
                            address.setLine2(address2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_Code);
                            address.setUser(user);

                            session.save(address);
                        }
                    }
                }

            }

        } else {
            responseJsonObject.addProperty("message", "User Not Sign In");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));

    }

    private void saveOrder(Session session, Transaction transaction, User user, Address address, JsonObject responseJsonObject) {
        try {

            //Create Order DB
            Orders orders = new Orders();
            orders.setAddress(address);
            orders.setDate_time(new Date());
            orders.setUser(user);

            int order_id = (int) session.save(orders);

            //Get Cart Item
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();

            //Create Order Item
            //Order Status = 1=paid
            //Order Status = 5 = payment Pending
            Order_Status order_Status = (Order_Status) session.get(Order_Status.class, 5);

            double SubTotal = 0;
            SubTotal += 250;
            String items = "";
            //Order Item 
            for (Cart cartItem : cartList) {
                //Cal Price
                SubTotal += cartItem.getQty() * cartItem.getProduct().getPrice();

                //Cal items 
                items += cartItem.getProduct().getTitle() + " x" + cartItem.getQty() + " ";

                //get product
                Product product = cartItem.getProduct();

                Order_Item order_Item = new Order_Item();
                order_Item.setOrders(orders);
                order_Item.setOrder_status(order_Status);
                order_Item.setProduct(product);
                order_Item.setQty(cartItem.getQty());
                session.save(order_Item);

                //update product qty
                product.setQty(product.getQty() - cartItem.getQty());
                session.update(product);

                //delete cart item
                session.delete(cartItem);
            }
            transaction.commit();

            String merchant_id = "1228238";
            String formatedAmount = new DecimalFormat("0.00").format(SubTotal);
            String currency = "LKR";
            String merchantSecret = "MTQ0MDA3NjQ0NjE3MDkyOTgyNjk0MTMzODU4NDEzMjIxODAxNjU0Nw==";
            String merchantSecretMd5Hash = PayHere.generateMD5(merchantSecret);

            //Payhere Payment data
            JsonObject payhere = new JsonObject();
            payhere.addProperty("merchant_id", merchant_id);

            payhere.addProperty("return_url", merchantSecret);
            payhere.addProperty("cancel_url", "");
            payhere.addProperty("notify_url", ""); //***

            payhere.addProperty("first_name", user.getFirst_name());
            payhere.addProperty("last_name", user.getLast_name());
            payhere.addProperty("email", user.getEmail());

            payhere.addProperty("phone", "");
            payhere.addProperty("address", "");
            payhere.addProperty("city", "Sri Lanka");
            payhere.addProperty("country", "Sri Lanka");

            payhere.addProperty("order_id", String.valueOf(order_id));
            payhere.addProperty("items", items);
            payhere.addProperty("currency", currency);
            payhere.addProperty("amount", formatedAmount);
            payhere.addProperty("sandbox", true);

            //getMd5(merahantID + orderID + amountFormatted + currency + getMd5(merchantSecret));
            String md5Hash = PayHere.generateMD5(merchant_id + order_id + formatedAmount + currency + merchantSecretMd5Hash);
            payhere.addProperty("hash", md5Hash);
            //Payhere Payment data

            responseJsonObject.addProperty("success", true);
            responseJsonObject.addProperty("message", "CheckOut Completed");

            Gson gson = new Gson();

            responseJsonObject.add("payhereJson", gson.toJsonTree(payhere));

        } catch (Exception e) {
            transaction.rollback();
        }
    }

}
