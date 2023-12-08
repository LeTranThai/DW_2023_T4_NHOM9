package warehouse.com.vn.control;

import warehouse.com.vn.model.PriceGold;
import warehouse.com.vn.service.PriceGoldService;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "PriceGoldController", value = "/PriceGoldController")
public class PriceGoldController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = request.getParameter("command");
        if (command.equals("detail")) {
            String id = request.getParameter("idP");
            List<PriceGold> listGoldByProvince = PriceGoldService.getAllGoldByIdProvince(Integer.parseInt(id));
            request.setAttribute("listGoldByProvince", listGoldByProvince);
            request.getRequestDispatcher("detail.jsp").forward(request, response);
//            content = "Truy cập trang quản lí chi tiết đơn hàng";
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
