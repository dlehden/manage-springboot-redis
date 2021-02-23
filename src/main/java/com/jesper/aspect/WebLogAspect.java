package com.jesper.aspect;


import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 리모델링 작업 
 */
@Component
@Aspect
public class WebLogAspect {

    private Map<Long, Map<String, List<Long>>> threadMap = new ConcurrentHashMap<>(200);


   //com.jesper.controller 패키지 및 하위 패키지 아래에있는 모든 클래스의 모든 메서드를 일치시킵니다.
    @Pointcut("execution(* com.jesper.controller..*.*(..))")
    public void executeService(){

    }
    /**
     * 메서드가 호출되기 전에 호출되는 사전 알림
     * @param joinPoint
     */
    @Before("executeService()")
    public void doBeforeAdvice(JoinPoint joinPoint){
        System.out.println("해당 클래스 시작전 ----------" + " " +joinPoint.toShortString() + " start");


        Map<String, List<Long>> methodTimeMap = threadMap.get(Thread.currentThread().getId());
        List<Long> list;
        if (methodTimeMap == null) {
            methodTimeMap = new HashMap<>();
            list = new LinkedList<>();
            list.add(System.currentTimeMillis());
            methodTimeMap.put(joinPoint.toShortString(), list);
            threadMap.put(Thread.currentThread().getId(), methodTimeMap);
        } else {
            list = methodTimeMap.get(joinPoint.toShortString());
            if (list == null) list = new LinkedList<>();
            list.add(System.currentTimeMillis());
            methodTimeMap.put(joinPoint.toShortString(), list);
        }

    }
    @After("executeService()")
    public void doAfterAdvice(JoinPoint joinPoint){
        System.out.println("해당 클래스 시작 후  ----------" + " " +joinPoint.toShortString() + " start");

    	// 대상 메소드의 매개 변수 정보 가져 오기
        Object[] obj = joinPoint.getArgs();
        // AOP 프록시 클래스 정보
        joinPoint.getThis();
        // 에이전트의 대상 개체
        joinPoint.getTarget();
        // 가장 많이 사용되는 알림의 서명
        Signature signature = joinPoint.getSignature();
        // 어떤 방법이 에이전트인지
        System.out.println("프록시 방법:" + signature.getName());
        //AOP프록시 클래스의 이름
        System.out.println("AOP프록시 클래스의 이름" + signature.getDeclaringTypeName());
        //AOP 프록시 클래스의 클래스 정보
        signature.getDeclaringType();
        //RequestAttributes 가져오기 
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        //Get RequestAttributes에서 HttpServletRequest 정보 가져 오기
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);
        //세션 정보를 얻으려면 다음과 같이 작성할 수 있습니다.
        //HttpSession session = (HttpSession) requestAttributes.resolveReference(RequestAttributes.REFERENCE_SESSION);
        Enumeration<String> enumeration = request.getParameterNames();
        Map<String,String> parameterMap = new HashMap<>();
        while (enumeration.hasMoreElements()){
            String parameter = enumeration.nextElement();
            parameterMap.put(parameter,request.getParameter(parameter));
        }
        String str = JSON.toJSONString(parameterMap);
        if(obj.length > 0) {
            System.out.println("요청 된 매개 변수 정보는 다음과 같습니다.："+str);
        }

        System.out.println(joinPoint.toShortString() + " end ");
        Map<String, List<Long>> methodTimeMap = threadMap.get(Thread.currentThread().getId());
        List<Long> list = methodTimeMap.get(joinPoint.toShortString());
        System.out.println("프록시 방법:" + signature.getName() + ", 사용 시간 ：" +
                (System.currentTimeMillis() - list.get(list.size() - 1)));
        list.remove(list.size() - 1);
    }
}
