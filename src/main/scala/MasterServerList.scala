/*
 * Copyright 2018 https://github.com/2m/master-server-stream/graphs/contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lt.dvim.vent

import akka.actor.ActorSystem
import akka.io.Udp
import akka.stream.scaladsl.{Source, Tcp}
import akka.stream.{ActorMaterializer, Materializer}
import lt.dvim.msqp.{IpPort, Region, Request}
import scodec.protocols.ip.Port
import scodec.protocols.ip.v4.Address

import scala.io.StdIn

object MasterServerList {
  def main(args: Array[String]): Unit = {

    implicit val sys = ActorSystem("MasterServerList")
    implicit val mat = ActorMaterializer()

    try {
      run()

      StdIn.readLine()
    } finally {
      sys.terminate()
    }

  }

  def run()(implicit sys: ActorSystem, mat: Materializer): Unit = {

    Source.single(Request(Region.Europe, IpPort(Address.fromStringValid("0.0.0.0"), Port(0)), ""))

    Tcp

  }
}
