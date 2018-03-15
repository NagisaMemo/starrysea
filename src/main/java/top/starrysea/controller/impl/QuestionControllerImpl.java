package top.starrysea.controller.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import top.starrysea.common.ModelAndViewFactory;
import top.starrysea.common.ServiceResult;
import top.starrysea.controller.IQuestionController;
import top.starrysea.object.dto.Question;
import top.starrysea.object.view.in.QuestionForAll;
import top.starrysea.object.view.in.QuestionForAnswer;
import top.starrysea.object.view.in.QuestionForAsk;
import top.starrysea.service.IQuestionService;
import static top.starrysea.common.Const.*;
import static top.starrysea.common.ResultKey.*;

@Controller
public class QuestionControllerImpl implements IQuestionController {

	@Autowired
	private IQuestionService questionService;

	@Override
	@RequestMapping(value = "/question", method = RequestMethod.GET)
	public ModelAndView queryQuestionController(QuestionForAll question, Device device) {
		question.setQuestionStatus((short) 2);
		ServiceResult serviceResult = questionService.queryAllQuestionService(question.getCondition(),
				question.toDTO());
		List<Question> result = serviceResult.getResult(LIST_1);
		List<top.starrysea.object.view.out.QuestionForAll> voResult = result.stream().map(Question::toVoForAll)
				.collect(Collectors.toList());
		return new ModelAndView(QUESTION + "question").addObject("result", voResult)
				.addObject("nowPage", serviceResult.getNowPage()).addObject("totalPage", serviceResult.getTotalPage());
	}

	@Override
	@RequestMapping(value = "/question/ajax", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> queryQuestionControllerAjax(@RequestBody QuestionForAll question) {
		ServiceResult serviceResult = questionService.queryAllQuestionService(question.getCondition(),
				question.toDTO());
		List<Question> result = serviceResult.getResult(LIST_1);
		List<top.starrysea.object.view.out.QuestionForAll> voResult = result.stream().map(Question::toVoForAll)
				.collect(Collectors.toList());
		Map<String, Object> theResult = new HashMap<>();
		theResult.put("result", voResult);
		theResult.put("nowPage", serviceResult.getNowPage());
		theResult.put("totalPage", serviceResult.getTotalPage());
		return theResult;
	}

	@Override
	@RequestMapping(value = "/question/ask", method = RequestMethod.POST)
	public ModelAndView askQuestionController(@Valid QuestionForAsk question, BindingResult bindingResult,
			Device device) {
		questionService.askQuestionService(question.toDTO());
		return ModelAndViewFactory.newSuccessMav("提问成功！", device);
	}

	@Override
	@RequestMapping(value = "/question/answer", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> answerQuestionController(@RequestBody @Valid QuestionForAnswer question,
			BindingResult bindingResult) {
		questionService.answerQuestionService(question.toDto());
		Map<String, Object> theResult = new HashMap<>();
		theResult.put("result", true);
		return theResult;
	}

}
