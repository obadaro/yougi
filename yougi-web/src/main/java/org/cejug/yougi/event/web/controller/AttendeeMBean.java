 /* Yougi is a web application conceived to manage user groups or
 * communities focused on a certain domain of knowledge, whose members are
 * constantly sharing information and participating in social and educational
 * events. Copyright (C) 2011 Hildeberto Mendonça.
 *
 * This application is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This application is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * There is a full copy of the GNU Lesser General Public License along with
 * this library. Look for the file license.txt at the root level. If you do not
 * find it, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA.
 * */
package org.cejug.yougi.event.web.controller;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import org.cejug.yougi.event.business.AttendeeBean;
import org.cejug.yougi.event.entity.Attendee;
import org.cejug.yougi.event.entity.Event;

/**
 * @author Hildeberto Mendonca - http://www.hildeberto.com
 */
@ManagedBean
@SessionScoped
public class AttendeeMBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private AttendeeBean attendeeBean;

    @ManagedProperty(value = "#{param.id}")
    private String id;

    private Attendee attendee;
    
    private List<Event> attendedEvents;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Attendee getAttendee() {
        return this.attendee;
    }

    public void setAttendee(Attendee attendee) {
        this.attendee = attendee;
    }
    
    public List<Event> getAttendedEvents() {
        if(this.attendedEvents == null) {
            this.attendedEvents = attendeeBean.findAttendeedEvents(this.attendee.getUserAccount());
        }
        return this.attendedEvents;
    }

    @PostConstruct
    public void load() {
        if (this.id != null && !this.id.isEmpty()) {
            this.attendee = attendeeBean.findAttendee(id);
        }
    }
}