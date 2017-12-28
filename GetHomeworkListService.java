package com.tianwen.eeducation.terminal.interfaces.exercise;

import com.huawei.imp.framework.utils.BeanHolder;
import com.tianwen.eeducation.common.tools.DateUtil;
import com.tianwen.eeducation.common.tools.JsonTools;
import com.tianwen.eeducation.common.tools.StringTools;
import com.tianwen.eeducation.server.domain.request.courseRecord.GetCourseRecordOfTeacherReq;
import com.tianwen.eeducation.server.domain.request.paper.GetHomeworkListServerReq;
import com.tianwen.eeducation.server.domain.response.paper.GetHomeworkListServerRsp;
import com.tianwen.eeducation.server.domain.response.paper.GetHomeworkListServerRsp.PublishInfo;
import com.tianwen.eeducation.server.service.CourseRecordService;
import com.tianwen.eeducation.server.service.ExerciseService;
import com.tianwen.eeducation.terminal.common.PortalException;
import com.tianwen.eeducation.terminal.interfaces.ServiceIF;
import com.tianwen.eeducation.terminal.tools.LogHelper;
import com.tianwen.eeducation.terminal.tools.ServiceUtil;
import com.tianwen.eeducation.terminal.type.request.exercise.GetHomeworkListReq;
import com.tianwen.eeducation.terminal.type.response.exercise.GetHomeworkListRsp;
import com.tianwen.eeducation.terminal.type.response.exercise.GetHomeworkListRsp.PublishInfo;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

public class GetHomeworkListService implements ServiceIF {
	private ExerciseService exerciseSvr = (ExerciseService)BeanHolder.getBean("exerciseSvr");
	private CourseRecordService courseRecordService = (CourseRecordService)BeanHolder.getBean("courseRecordService");
	private static final String ONLINE_EXAM_TYPE = "20";
	
	public void service(HttpServletRequest request, HttpServletResponse response) throws Exception {

		GetHomeworkListReq req = (GetHomeworkListReq)ServiceUtil.getRequest(request, GetHomeworkListReq.class);
		
		validateParameter(req);
		
		GetHomeworkListServerReq serverReq = new GetHomeworkListServerReq();
		
		formServerRequest(req, serverReq);
		if (null == serverReq.getCount()) {
			serverReq.setCount(Integer.valueOf(Integer.MAX_VALUE));
		}
		GetHomeworkListServerRsp serverRsp = exerciseSvr.getHomeworkList(serverReq);
		
		GetHomeworkListRsp rsp = new GetHomeworkListRsp();
		
		formHomeWorkResponse(serverRsp, rsp);
		
		GetCourseRecordOfTeacherReq getCourseRecordOfTeacherReq = new GetCourseRecordOfTeacherReq();
		getCourseRecordOfTeacherReq.setQueryUserId(req.getUserAccount());
		getCourseRecordOfTeacherReq.setQueryUserType("1");
		getCourseRecordOfTeacherReq.setCourseRecordType("2");
		getCourseRecordOfTeacherReq.setLastDeleteQuestTime(req.getLastDeleteQuestTime());
		rsp.setDeletedRecordIdList(courseRecordService.getDeletedCourseRecordList(getCourseRecordOfTeacherReq));
		
		String jsonRsp = JsonTools.getJsonFromObject(rsp);
		
		ServiceUtil.response(jsonRsp, request, response);
	}
	
	private void validateParameter(GetHomeworkListReq req) throws PortalException {

		if ((null == req) || (StringUtils.isEmpty(req.getUserAccount()))) {
			LogHelper.msgLocal.set("GetHomeworkListReq or the user account attribute is empty.");
			throw new PortalException(101002);
		}

		if (StringUtils.isNotBlank(req.getStatus())) {
			String ligalStatus = "1,2,3,4,5";
			String[] statusAttr = StringUtils.split(req.getStatus(), ',');
			for (int i = 0; i < statusAttr.length; i++) {
				if (ligalStatus.indexOf(statusAttr[i]) < 0) {
					throw new PortalException(Integer.valueOf(101001), "Invalid status!");
				}
			}
		}
	}
	
	private void formHomeWorkResponse(GetHomeworkListServerRsp serverRsp, GetHomeworkListRsp rsp)
	{
		if ((serverRsp == null) || (CollectionUtils.isEmpty(serverRsp.getPublishInfoList()))) {
			return;
		}
		rsp.setTotalRecord(serverRsp.getTotalRecord());
		List<GetHomeworkListServerRsp.PublishInfo> serverPublishInfoList = serverRsp.getPublishInfoList();
		SimpleDateFormat sdf = DateUtil.getStandardFormate();
		
		List<GetHomeworkListRsp.PublishInfo> publishInfoList = new ArrayList();
		for (GetHomeworkListServerRsp.PublishInfo serverPublishInfo : serverPublishInfoList)
		{
			String subjectName = serverPublishInfo.getSubjectName();
			
			GetHomeworkListRsp.PublishInfo publishInfo = new GetHomeworkListRsp.PublishInfo();
			publishInfo.setSubjectName(subjectName);
			publishInfo.setSubjectId(serverPublishInfo.getSubjectid() == null ? "-1" : serverPublishInfo.getSubjectid()
				.toString());
			publishInfo.setPublishId(serverPublishInfo.getPublishId());
			publishInfo.setPaperId(serverPublishInfo.getPaperId());
			publishInfo.setQuestionCount(serverPublishInfo.getQuestionCount());
			publishInfo.setClassId(serverPublishInfo.getClassId());
			if (null != serverPublishInfo.getLimitTime()) {
				publishInfo.setLimitTime(serverPublishInfo.getLimitTime().toString());
			}
			publishInfo.setPublishTime(serverPublishInfo.getPublishTime());
			if (null != serverPublishInfo.getSpendTime()) {
				publishInfo.setSpendTime(serverPublishInfo.getSpendTime().toString());
			}
			publishInfo.setBookId(serverPublishInfo.getBookId());
			publishInfo.setPageNum(serverPublishInfo.getPageNum());
			if (null != serverPublishInfo.getGrade())
			{
				publishInfo.setGrade(serverPublishInfo.getGrade().toString());
				publishInfo.setGradeName(serverPublishInfo.getGradeName());
			}
			publishInfo.setStatus(serverPublishInfo.getPublishStatus());
			publishInfo.setEndStatus(serverPublishInfo.getEndStatus());
			
			publishInfo.setTerm(serverPublishInfo.getTerm());
			publishInfo.setTermName(serverPublishInfo.getTermName());
			publishInfo.setTitle(serverPublishInfo.getTitle());
			publishInfo.setEvaluateType(serverPublishInfo.getEvaluateType());
			publishInfo.setIsTimeLimit(serverPublishInfo.getIsTimeLimit());
			publishInfo.setCanReSubmit(serverPublishInfo.getCanReSubmit());
			publishInfo.setFinishTime(serverPublishInfo.getFinishTime());
			publishInfo.setScheduleId(serverPublishInfo.getScheduleId());
			publishInfo.setClassName(serverPublishInfo.getClassName());
			if (null != serverPublishInfo.getExportFile()) {
				publishInfo.setJsonPath(serverPublishInfo.getExportFile());
			}
			if ("8".equalsIgnoreCase(serverPublishInfo.getPapertype()))
			{
				publishInfo.setPublishType("20");
				publishInfo.setExamTypeValue(serverPublishInfo.getExamTypeValue());
			}
			else
			{
				publishInfo.setPublishType(serverPublishInfo.getPapertype());
			}
			if ("5".equals(serverPublishInfo.getPapertype()))
			{
				publishInfo.setIsGame("1");
				publishInfo.setTemplatePath(serverPublishInfo.getTemplatePath());
				
				Integer finishCount = Integer.valueOf((null == serverPublishInfo.getCorrectNum() ? 0 : serverPublishInfo.getCorrectNum().intValue()) + (null == serverPublishInfo
					.getWrongNum() ? 0 : serverPublishInfo.getWrongNum().intValue()));
				publishInfo.setFinishCount(finishCount);
			}
			else
			{
				publishInfo.setIsGame("0");
			}
			if ((null != serverPublishInfo.getAnswerDuration()) && (serverPublishInfo.getAnswerDuration().intValue() > 0)) {
				publishInfo.setAnswerDuration(Integer.valueOf(serverPublishInfo.getAnswerDuration().intValue() / 60));
			}
			if (null != serverPublishInfo.getPaperOpenTime()) {
				publishInfo.setPaperOpenTime(sdf.format(serverPublishInfo.getPaperOpenTime()));
			}
			publishInfoList.add(publishInfo);
		}
		if (publishInfoList.size() > 0) {
			rsp.setPublishInfoList(publishInfoList);
		}
	}
	
	private void formServerRequest(GetHomeworkListReq req, GetHomeworkListServerReq serverReq)
	{
		if ("20".equalsIgnoreCase(req.getType())) {
			serverReq.setType("8");
		} else {
			serverReq.setType(req.getType());
		}
		serverReq.setCount(req.getCount());
		serverReq.setStart(req.getStart());
		serverReq.setStatus(req.getStatus());
		serverReq.setDelFlag(req.getDelFlag());
		
		serverReq.setExamValueList(req.getExamValueList());
		if ("1".equals(serverReq.getStatus())) {
			serverReq.setStatus("1,2");
		}
		String publishIdString = req.getPublishId();
		if (StringTools.isEmpty(publishIdString)) {
			serverReq.setQueryAll(true);
		} else {
			serverReq.setPublishId(publishIdString);
		}
		serverReq.setStartTime(req.getStartTime());
		serverReq.setEndTime(req.getEndTime());
		if (StringUtils.isNotEmpty(req.getSubjectId()))
		{
			String[] subjectArray = req.getSubjectId().split(",");
			List<String> subjectIdList = new ArrayList();
			if (!ArrayUtils.isEmpty(subjectArray)) {
				for (int i = 0; i < subjectArray.length; i++) {
					subjectIdList.add(subjectArray[i]);
				}
			}
			serverReq.setSubjectIdList(subjectIdList);
		}
		serverReq.setUserAccount(req.getUserAccount());
		serverReq.setScheduleId(req.getScheduleId());
		serverReq.setTitle(req.getTitle());
	}
}

/* Location:
 * Qualified Name:		 GetHomeworkListService
 * Java Class Version: 8 (52.0)
 * JD-Core Version:		0.7.1
 */
