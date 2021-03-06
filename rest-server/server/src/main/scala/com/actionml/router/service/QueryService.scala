/*
 * Copyright ActionML, LLC under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * ActionML licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actionml.router.service

import akka.actor.ActorSystem
import com.actionml.admin.Administrator
import com.actionml.core.model.Response
import com.actionml.core.validate.JsonSupport

import scala.concurrent.{ExecutionContext, Future}

/**
  *
  *
  * @author The ActionML Team (<a href="http://actionml.com">http://actionml.com</a>)
  * 25.02.17 11:48
  */
trait QueryService {
  def queryAsync(engineId: String, query: String): Future[Response]
}

class QueryServiceImpl(admin: Administrator, system: ActorSystem) extends QueryService with JsonSupport {
  implicit val esEC: ExecutionContext = system.dispatchers.lookup("es-dispatcher")

  override def queryAsync(engineId: String, query: String): Future[Response] =
    admin.getEngine(engineId) match {
      case Some(engine) ⇒ engine.queryAsync(query)
      case None ⇒ Future.failed(new RuntimeException(jsonComment(s"Engine for id=$engineId not found")))
    }
}

sealed trait QueryAction
case class GetPrediction(engineId: String, query: String) extends QueryAction
