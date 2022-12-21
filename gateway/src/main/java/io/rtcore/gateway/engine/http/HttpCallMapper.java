package io.rtcore.gateway.engine.http;

import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.ResponseInfo;

import io.rtcore.gateway.engine.ServerTxnHandle;

public interface HttpCallMapper {

  URI uri();

  String method();

  BodyPublisher bodyPublisher();

  BodySubscriber<Void> bodySubscriber(ResponseInfo resInfo, ServerTxnHandle handle);

}
