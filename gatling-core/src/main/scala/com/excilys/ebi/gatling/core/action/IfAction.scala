/**
 * Copyright 2011-2012 eBusiness Information, Groupe Excilys (www.excilys.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.excilys.ebi.gatling.core.action

import com.excilys.ebi.gatling.core.session.Session
import akka.actor.ActorRef
import grizzled.slf4j.Logging

/**
 * A conditional Action
 *
 * @constructor create an IfAction
 * @param condition the condition that decides whether to execute thenNext or elseNext
 * @param thenNext the chain of actions executed if condition evaluates to true
 * @param elseNext chain of actions executed if condition evaluates to false
 * @param next chain of actions executed if condition evaluates to false and elseNext equals None
 */
class IfAction(condition: Session => Boolean, thenNext: ActorRef, elseNext: Option[ActorRef], next: ActorRef) extends Action with Logging {

	/**
	 * Evaluates the condition and decides what to do next
	 *
	 * @param session the session of the virtual user
	 */
	def execute(session: Session) {

		val nextAction =
			try {
				if (condition(session)) thenNext else elseNext.getOrElse(next)
			} catch {
				case e =>
					error("'if' condition evaluation crashed, exiting block for this user", e)
					next
			}
		nextAction ! session
	}
}