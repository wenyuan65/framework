package com.wy.panda.mvc.result;

import com.wy.panda.mvc.ServletContext;
import com.wy.panda.mvc.domain.Request;
import com.wy.panda.mvc.domain.Response;

import java.util.concurrent.CompletableFuture;

public class AsyncResult extends AbstractResult{

    private CompletableFuture<Result> future;

    public AsyncResult(CompletableFuture<Result> future) {
        this.future = future;
    }

    @Override
    public void prepare(Request request, Response response) {
    }

    @Override
    public void doRender(Request request, Response response) {
        ServletContext servletContext = request.getServletContext();

        //TODO： 将异步结果封装成任务，放在线程池中等待操作结果，退出当前线程


    }
}
