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

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorSystem, Props}
import akka.stream.alpakka.udp.UdpMessage
import akka.stream.alpakka.udp.scaladsl.Udp
import akka.stream.scaladsl.SourceQueueWithComplete
import akka.stream.scaladsl.Sink
import akka.stream.scaladsl.Source
import akka.stream.{ActorMaterializer, Materializer, OverflowStrategy}
import akka.util.ByteString
import lt.dvim.msqp.{IpPort, Region, Request, Response}
import scodec.Codec
import scodec.bits.BitVector
import scodec.protocols.ip.Port
import scodec.protocols.ip.v4.Address

import scala.io.StdIn

class ServerStreamActor(masterServer: InetSocketAddress) extends Actor {

  implicit val sys = context.system
  implicit val mat = ActorMaterializer()

  val bindFlow = Udp.bindFlow(new InetSocketAddress(0))
  val queue: SourceQueueWithComplete[Request] = Source
    .queue[Request](bufferSize = 4, OverflowStrategy.fail)
    .map(Codec.encode(_))
    .map(attempt => ByteString(attempt.toOption.get.toByteArray))
    .map(UdpMessage(_, masterServer))
    .via(bindFlow)
    .map(msg => BitVector(msg.data.toArray))
    .map(Codec.decode[Response](_).require.value)
    .to(Sink.foreach(self ! _))
    .run()

  var serversSoFar = 0

  override def receive = {
    case r: Request => queue.offer(r)
    case Response(servers) =>
      serversSoFar += servers.size
      println(s"Got no of servers [${servers.size}], so far [$serversSoFar]")
      println(s"Last server [${servers.last}]")
      self ! Request(Region.Europe, servers.last, "")
  }
}

object MasterServerList {
  def main(args: Array[String]): Unit = {
    val sys = ActorSystem("MasterServerList")

    val masterServer = new InetSocketAddress("hl2master.steampowered.com", 27011)
    val stream = sys.actorOf(Props(new ServerStreamActor(masterServer)))

    val initialRequest =
      Request(Region.Europe, IpPort(Address.fromStringValid("0.0.0.0"), Port(0)), "")
    stream ! initialRequest

    StdIn.readLine()
    sys.terminate()
  }
}
