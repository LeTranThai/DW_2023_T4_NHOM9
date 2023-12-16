<%@ page import="warehouse.com.vn.model.PriceGold" %>
<%@ page import="java.util.List" %>
<%@ page import="warehouse.com.vn.service.PriceGoldService" %>
<%@ page import="warehouse.com.vn.model.TypeGold_dim" %>
<%@ page import="java.util.Collections" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="style.css">
</head>
<body>
<% List<PriceGold> priceGoldProduct = (List<PriceGold>) request.getAttribute("listGoldByProvince");%>

<div>
    <H1>GIÁ VÀNG HÀNG NGÀY</H1>
    <table class="table">
        <thead>
        <tr>
            <th class="text-left">Khu vực</th>
            <th class="text-left">Hệ thống</th>
            <th>Mua vào</th>
            <th>Bán ra</th>
        </tr>
        </thead>
        <tbody>

        <%
            for (int i = 0; i < priceGoldProduct.size(); i++) {
        %>
        <tr>
            <td class="text-left">
                <a href="admin-order?command=detail&IdCk=<%=priceGoldProduct.get(i).getIdProvince()%>"><%=PriceGoldService.getProvince(priceGoldProduct.get(i).getIdProvince())%>
                </a>
            </td>
            <td class="text-left">
                <%=PriceGoldService.getTypeGold(priceGoldProduct.get(i).getIdType())%>
            </td>
            <td class="gold-price"><%=priceGoldProduct.get(i).getPriceBuy()%>
            </td>
            <td class="gold-price"><%=priceGoldProduct.get(i).getPriceSell()%>
            </td>
        </tr>
        <%}%>
        </tbody>
        <tfoot>
        <tr>
            <td colspan="4" class="update-info">
                Cập nhật lúc <%=priceGoldProduct.get(0).getDateStart()%><br>
                <a href="https://giavang.org/" title="Giá vàng - ">https://giavang.org/</a>
            </td>
        </tr>
        </tfoot>
    </table>
</div>
<script>
    function goldProvince(id) {
        window.location.href = "detail?id=" + id;
    }
</script>
</body>
</html>
