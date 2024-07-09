/*
 * The MIT License.
 *
 * Copyright (c) Dysaido <tonyyoni@gmail.com>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package xyz.dysaido.pvpevent.api.model;

import org.bukkit.event.entity.PlayerDeathEvent;
import xyz.dysaido.pvpevent.api.PvPEvent;
import xyz.dysaido.pvpevent.match.MatchState;
import xyz.dysaido.pvpevent.match.Participant;
import xyz.dysaido.pvpevent.match.ParticipantStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface Match<I> {

    void join(I identifier);

    void leave(I identifier);

    void spectate(I identifier);

    void onDeath(I identifier, PlayerDeathEvent event);

    void onDestroy();

    Match<I> onCreate(PvPEvent pvpEvent, List<Integer> announcements);

    void onStartTask();

    void onPause();

    void onStopTask();

    void nextRound();

    boolean isOver();

    boolean isFull();

    boolean isInsufficient();

    void setState(MatchState state);

    MatchState getState();

    boolean hasParticipant(I identifier);

    ParticipantStatus getParticipantStatus(I identifier);

    Map<I, Participant> getParticipantsByUUD();

    Map<I, ParticipantStatus> getStatusByUUID();
}
