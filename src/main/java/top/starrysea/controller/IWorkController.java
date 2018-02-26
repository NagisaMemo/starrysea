package top.starrysea.controller;

import java.util.Map;

import org.springframework.mobile.device.Device;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import top.starrysea.object.view.in.WorkForAdd;
import top.starrysea.object.view.in.WorkForAll;
import top.starrysea.object.view.in.WorkForOne;
import top.starrysea.object.view.in.WorkTypeForModify;
import top.starrysea.object.view.in.WorkTypeForRemove;

public interface IWorkController {

	ModelAndView queryAllWorkController(WorkForAll work, Device device);

	Map<String, Object> queryAllWorkControllerAjax(WorkForAll work);

	ModelAndView queryWorkController(WorkForOne work, BindingResult bindingResult, Device device);

	Map<String, Object> queryWorkControllerAjax(WorkForOne work, BindingResult bindingResult);

	ModelAndView addWorkController(MultipartFile coverFile, MultipartFile[] imageFiles, WorkForAdd work,
			BindingResult bindingResult, Device device);

	ModelAndView removeWorkController(WorkForOne work, BindingResult bindingResult, Device device);

	ModelAndView removeWorkTypeController(WorkTypeForRemove workType, BindingResult bindingResult, Device device);

	ModelAndView modifyWorkTypeController(WorkTypeForModify workType, BindingResult bindingResult, Device device);
}
