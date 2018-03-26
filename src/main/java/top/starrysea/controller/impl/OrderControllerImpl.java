package top.starrysea.controller.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import top.starrysea.common.Common;
import top.starrysea.common.ModelAndViewFactory;
import top.starrysea.common.ServiceResult;
import top.starrysea.controller.IOrderController;
import top.starrysea.object.dto.OrderDetail;
import top.starrysea.object.dto.Orders;
import top.starrysea.object.dto.WorkType;
import top.starrysea.object.view.in.OrderDetailForAddOrder;
import top.starrysea.object.view.in.OrderDetailForModifyAddr;
import top.starrysea.object.view.in.OrderForAdd;
import top.starrysea.object.view.in.OrderForAddress;
import top.starrysea.object.view.in.OrderForAll;
import top.starrysea.object.view.in.OrderForModify;
import top.starrysea.object.view.in.OrderForOne;
import top.starrysea.object.view.in.OrderForRemove;
import top.starrysea.object.view.in.WorkTypeForToAddOrders;
import top.starrysea.object.view.in.WorkTypesForRemoveCar;
import top.starrysea.security.SecurityAlgorithm;
import top.starrysea.object.view.in.WorkTypeForRemoveCar;
import top.starrysea.service.IOrderService;

import static top.starrysea.common.Const.*;
import static top.starrysea.common.ResultKey.*;

@Controller
public class OrderControllerImpl implements IOrderController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private IOrderService orderService;
	@Resource(name = "desede")
	private SecurityAlgorithm desede;

	@Override
	// 查询所有的订单
	@RequestMapping(value = "/order", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryAllOrderController(@RequestBody OrderForAll order) {
		ServiceResult serviceResult = orderService.queryAllOrderService(order.getCondition(), order.toDTO());
		List<Orders> result = serviceResult.getResult(LIST_1);
		List<top.starrysea.object.view.out.OrderForAll> voResult = result.stream().map(Orders::toVoForAll)
				.collect(Collectors.toList());
		Map<String, Object> theResult = new HashMap<>();
		theResult.put("orderName", order.getOrderName());
		theResult.put("result", voResult);
		theResult.put("nowPage", serviceResult.getNowPage());
		theResult.put("totalPage", serviceResult.getTotalPage());
		return theResult;
	}

	@Override
	// 根据订单号查询一个订单的具体信息以及发货情况
	@RequestMapping(value = "/order/{orderNum}", method = RequestMethod.GET)
	public ModelAndView queryOrderController(@Valid OrderForOne order, BindingResult bindingResult, Device device) {
		ServiceResult serviceResult = orderService.queryOrderService(order.toDTO());
		Orders o = serviceResult.getResult(ORDER);
		List<OrderDetail> ods = serviceResult.getResult(LIST_1);
		return new ModelAndView(device.isMobile() ? MOBILE + "orders_details" : "orders_details")
				.addObject("order", o.toVoForOne())
				.addObject("orderDetails", ods.stream().map(OrderDetail::toVoForOne).collect(Collectors.toList()));
	}

	@Override
	// 根据订单号查询一个订单的具体信息以及发货情况
	@RequestMapping(value = "/order/detail/ajax", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryOrderControllerAjax(@RequestBody @Valid OrderForRemove order,
			BindingResult bindingResult) {
		ServiceResult serviceResult = orderService.queryOrderService(order.toDTO());
		Orders o = serviceResult.getResult(ORDER);
		List<OrderDetail> ods = serviceResult.getResult(LIST_1);
		Map<String, Object> theResult = new HashMap<>();
		theResult.put("orders", o.toVoForOne());
		theResult.put("orderId", order.getOrderId());
		theResult.put("orderDetails", ods.stream().map(OrderDetail::toVoForOne).collect(Collectors.toList()));
		return theResult;
	}

	@RequestMapping(value = "/order/toAddOrder", method = RequestMethod.POST)
	public ModelAndView gotoAddOrder(@Valid WorkTypeForToAddOrders workTypes, Device device, HttpSession session) {
		ServiceResult sr = orderService.queryWorkTypeStock(workTypes.toDTO());
		if (!sr.isSuccessed()) {
			return ModelAndViewFactory.newErrorMav(sr.getErrInfo(), device);
		}
		String token = Common.getCharId(10);
		session.setAttribute(TOKEN, token);
		return new ModelAndView(device.isMobile() ? MOBILE + "add_order" : "add_order")
				.addObject("workTypes", workTypes)
				.addObject("provinces", orderService.queryAllProvinceService().getResult(MAP)).addObject(TOKEN, token);
	}

	@Override
	// 对一个作品进行下单
	@RequestMapping(value = "/order/add", method = RequestMethod.POST)
	public ModelAndView addOrderController(@Valid OrderForAdd order, BindingResult bindingResult, Device device,
			HttpSession session) {
		if (!order.getToken().equals(session.getAttribute(TOKEN))) {
			return ModelAndViewFactory.newErrorMav("您已经下单,请勿再次提交", device);
		}
		session.removeAttribute(TOKEN);
		ServiceResult serviceResult = orderService.addOrderService(order.toDTO(), order.toDTOOrderDetail());
		if (!serviceResult.isSuccessed()) {
			return ModelAndViewFactory.newErrorMav(serviceResult.getErrInfo(), device);
		}
		orderService.removeShoppingCarListService(session.getId());
		return ModelAndViewFactory.newSuccessMav("您已下单成功，之后将会为您派送！", device);
	}

	@Override
	// 修改一个订单的状态
	@RequestMapping(value = "/order/modify/{orderId}", method = RequestMethod.POST)
	public ModelAndView modifyOrderController(@Valid OrderForModify order, BindingResult bindingResult, Device device) {
		orderService.modifyOrderService(order.toDTO());
		return ModelAndViewFactory.newSuccessMav("发货成功！", device);
	}

	@Override
	// 删除一个订单
	@RequestMapping(value = "/order/remove/{orderId}", method = RequestMethod.POST)
	public ModelAndView removeOrderController(@Valid OrderForRemove order, BindingResult bindingResult, Device device) {
		orderService.removeOrderService(order.toDTO());
		return ModelAndViewFactory.newSuccessMav("删除成功!", device);
	}

	@Override
	@RequestMapping(value = "/order/export", method = RequestMethod.GET)
	public void exportOrderToXlsController(HttpServletResponse response) {
		orderService.exportOrderToXlsService();
		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + "result.xls");
		try {
			byte[] buff = Files.readAllBytes(Paths.get("/result.xls"));
			response.getOutputStream().write(buff);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	@Override
	@RequestMapping(value = "/order/resend", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> resendEmailController(@RequestBody @Valid OrderForRemove order,
			BindingResult bindingResult) {
		orderService.resendEmailService(order.toDTO());
		Map<String, Object> theResult = new HashMap<>();
		theResult.put("result", "success");
		return theResult;
	}

	@Override
	@RequestMapping(value = "/car/add", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> addWorkToShoppingCarController(HttpSession session,
			@RequestBody @Valid OrderDetailForAddOrder orderDetail, BindingResult bindingResult, Device device) {
		List<OrderDetailForAddOrder> orderDetailList = orderService.queryShoppingCarListService(session.getId())
				.getResult(LIST_1);
		if (orderDetailList == null) {
			orderDetailList = new ArrayList<>();
		}
		Map<String, Object> theResult = new HashMap<>();

		for (OrderDetailForAddOrder orderDetailForAddOrder : orderDetailList) {
			if (orderDetailForAddOrder.getWorkId() == orderDetail.getWorkId()) {
				theResult.put(INFO, "您已经将该作品放入购物车,不能重复放入");
				return theResult;
			}
		}
		orderDetailList.add(orderDetail);
		orderService.addorModifyWorkToShoppingCarService(session.getId(), orderDetailList);
		theResult.put(INFO, "添加到购物车成功!");
		return theResult;
	}

	@Override
	@RequestMapping(value = "/car/remove/{index}", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView removeWorkFromShoppingCarController(HttpSession session, @Valid WorkTypeForRemoveCar workType,
			BindingResult bindingResult, Device device) {
		if (session.getAttribute(TOKEN) == null || !session.getAttribute(TOKEN).equals(workType.getToken())) {
			return ModelAndViewFactory.newErrorMav("您已经删除该作品,请勿再次提交", device);
		}
		session.removeAttribute(TOKEN);
		List<OrderDetailForAddOrder> orderDetailList = orderService.queryShoppingCarListService(session.getId())
				.getResult(LIST_1);
		orderDetailList.remove((int) workType.getIndex());
		orderService.addorModifyWorkToShoppingCarService(session.getId(), orderDetailList);
		return ModelAndViewFactory.newSuccessMav("从购物车移除作品成功!", device);
	}

	@Override
	@RequestMapping(value = "/car", method = RequestMethod.GET)
	public ModelAndView queryShoppingCarController(HttpSession session, Device device) {
		List<OrderDetailForAddOrder> orderDetailList = orderService.queryShoppingCarListService(session.getId())
				.getResult(LIST_1);
		if (orderDetailList == null) {
			orderDetailList = new ArrayList<>();
		}
		List<WorkType> workTypes = orderService.queryAllWorkTypeForShoppingCarService(orderDetailList.stream()
				.map(orderDetail -> new WorkType.Builder().workTypeId(orderDetail.getWorkTypeId()).build())
				.collect(Collectors.toList())).getResult(LIST_1);
		String token = Common.getCharId(10);
		session.setAttribute(TOKEN, token);
		return new ModelAndView(device.isMobile() ? MOBILE + "shopcar" : "shopcar")
				.addObject("workTypes", workTypes.stream().map(WorkType::toVoForCar).collect(Collectors.toList()))
				.addObject("orderDetails", orderDetailList).addObject(TOKEN, token);
	}

	@Override
	@RequestMapping(value = "/car/removes", method = RequestMethod.POST)
	public ModelAndView removeWorksFromShoppingCarController(HttpSession session,
			@Valid WorkTypesForRemoveCar workTypes, BindingResult bindingResult, Device device) {
		if (session.getAttribute(TOKEN) == null || !session.getAttribute(TOKEN).equals(workTypes.getToken())) {
			return ModelAndViewFactory.newErrorMav("您已经删除过这些作品,请勿再次提交", device);
		}
		session.removeAttribute(TOKEN);
		List<OrderDetailForAddOrder> orderDetailList = orderService.queryShoppingCarListService(session.getId())
				.getResult(LIST_1);
		for (WorkTypeForRemoveCar workType : workTypes.getWorkTypes()) {
			orderDetailList.remove((int) workType.getIndex());
		}
		orderService.addorModifyWorkToShoppingCarService(session.getId(), orderDetailList);
		return ModelAndViewFactory.newSuccessMav("从购物车移除作品成功!", device);
	}

	@Override
	@RequestMapping(value = "/order/address/modify", method = RequestMethod.POST)
	public ModelAndView modifyAddressController(HttpSession session, @Valid OrderForAddress order,
			BindingResult bindingResult, Device device) {
		Orders result = orderService.queryOrderService(order.toDTO()).getResult(ORDER);
		String key = (String) session.getAttribute(TOKEN);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = Common.toObject(desede.decrypt(key), Map.class);
		if (!map.get("areaName").equals(result.getOrderArea().getAreaName())
				|| !map.get("areaAddress").equals(result.getOrderAddress())) {
			return ModelAndViewFactory.newErrorMav("参数不正确，请重新获取链接", device);
		} else if (map.get("limitTime") == null || (long) map.get("limitTime") < new Date().getTime()) {
			return ModelAndViewFactory.newErrorMav("链接已过期，请重新获取链接", device);
		}
		orderService.modifyAddressService(order.toDTO());
		return ModelAndViewFactory.newSuccessMav("修改地址成功", device);
	}

	@RequestMapping(value = "/order/address/toModifyAddr/{orderNum}", method = RequestMethod.GET)
	public ModelAndView gotoModifyAddressController(HttpSession session, @Valid OrderDetailForModifyAddr order,
			BindingResult bindingResult, Device device) {
		ServiceResult serviceResult = orderService.queryOrderService(order.toDTO());
		Orders o = serviceResult.getResult(ORDER);
		@SuppressWarnings("unchecked")
		Map<String, Object> map = Common.toObject(desede.decrypt(order.getKey()), Map.class);
		if (!map.get("areaName").equals(o.getOrderArea().getAreaName())
				|| !map.get("areaAddress").equals(o.getOrderAddress())) {
			return ModelAndViewFactory.newErrorMav("参数不正确，请重新获取链接", device);
		} else if (map.get("limitTime") == null || (long) map.get("limitTime") < new Date().getTime()) {
			return ModelAndViewFactory.newErrorMav("链接已过期，请重新获取链接", device);
		}
		session.setAttribute(TOKEN, order.getKey());
		List<OrderDetail> ods = serviceResult.getResult(LIST_1);
		return new ModelAndView(device.isMobile() ? MOBILE + "orders_modify_address" : "orders_modify_address")
				.addObject("order", o.toVoForOne())
				.addObject("provinces", orderService.queryAllProvinceService().getResult(MAP))
				.addObject("orderDetails", ods.stream().map(OrderDetail::toVoForOne).collect(Collectors.toList()));
	}

	@Override
	@RequestMapping(value = "/order/address/send", method = RequestMethod.POST)
	public ModelAndView modifyAddressEmailController(@Valid OrderForOne order, BindingResult bindingResult,
			Device device) {
		ServiceResult result = orderService.modifyAddressEmailService(order.toDTO());
		if (!result.isSuccessed()) {
			return ModelAndViewFactory.newErrorMav("您的订单已发货,不能再修改收货地址!", device);
		}
		return ModelAndViewFactory.newSuccessMav("修改链接已发送至您的邮箱，请注意查收", device);
	}
}
