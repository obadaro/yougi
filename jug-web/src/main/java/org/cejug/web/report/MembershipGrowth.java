/* Jug Management is a web application conceived to manage user groups or 
 * communities focused on a certain domain of knowledge, whose members are 
 * constantly sharing information and participating in social and educational 
 * events. Copyright (C) 2011 Ceara Java User Group - CEJUG.
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
package org.cejug.web.report;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import org.cejug.business.UserAccountBsn;
import org.cejug.entity.UserAccount;
import org.cejug.web.util.ResourceBundleHelper;
import org.primefaces.model.chart.CartesianChartModel;
import org.primefaces.model.chart.ChartSeries;

/**
 * This class feeds the bar chart that shows the growth of the user group in a
 * monthly basis.
 * @author Hildeberto Mendonca  - http://www.hildeberto.com
 */
@ManagedBean
@RequestScoped
public class MembershipGrowth {
    
    @EJB
    private UserAccountBsn userAccountBsn;
    
    private CartesianChartModel membershipGrowthModel;
    private CartesianChartModel membershipCumulativeGrowthModel;
    
    static final Logger logger = Logger.getLogger("org.cejug.web.report.MembershipGrowth");

    public MembershipGrowth() {}

    public CartesianChartModel getMembershipGrowthModel() {
        return membershipGrowthModel;
    }
    
    public CartesianChartModel getMembershipCumulativeGrowthModel() {
        return membershipCumulativeGrowthModel;
    }

    @PostConstruct
    public void load() {
        membershipGrowthModel = new CartesianChartModel();
        membershipCumulativeGrowthModel = new CartesianChartModel();
        
        ResourceBundleHelper bundle = new ResourceBundleHelper();
        String[] months = {bundle.getMessage("januaryShort"),
                            bundle.getMessage("februaryShort"),
                            bundle.getMessage("marchShort"),
                            bundle.getMessage("aprilShort"),
                            bundle.getMessage("mayShort"),
                            bundle.getMessage("juneShort"),
                            bundle.getMessage("julyShort"),
                            bundle.getMessage("augustShort"),
                            bundle.getMessage("septemberShort"),
                            bundle.getMessage("octoberShort"),
                            bundle.getMessage("novemberShort"),
                            bundle.getMessage("decemberShort")};
        
        Calendar lastDay = Calendar.getInstance();
        lastDay.set(Calendar.MONTH, Calendar.DECEMBER);
        lastDay.set(Calendar.DATE, 31);
        lastDay.set(Calendar.HOUR, 11);
        lastDay.set(Calendar.MINUTE, 59);
        lastDay.set(Calendar.SECOND, 59);
        lastDay.set(Calendar.AM_PM, Calendar.PM);
        Date to = lastDay.getTime();
        
        Calendar firstDay = lastDay;
        firstDay.add(Calendar.YEAR, -1);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        firstDay.set(Calendar.MONTH, 0);
        firstDay.set(Calendar.HOUR, 0);
        firstDay.set(Calendar.MINUTE, 0);
        lastDay.set(Calendar.SECOND, 0);
        lastDay.set(Calendar.AM_PM, Calendar.AM);
        Date from = firstDay.getTime();
        
        List<UserAccount> userAccounts = userAccountBsn.findConfirmedUserAccounts(from, to);
        
        Calendar registrationDate;
        Calendar deactivationDate;
        int[][] data = new int[2][12];
        int year, month, currentYear = 0;
        for(UserAccount userAccount: userAccounts) {
            registrationDate = Calendar.getInstance();
            registrationDate.setTime(userAccount.getRegistrationDate());
            if(currentYear == 0) {
                currentYear = registrationDate.get(Calendar.YEAR);
            }
            
            year = registrationDate.get(Calendar.YEAR) - currentYear;
            month = registrationDate.get(Calendar.MONTH);
            data[year][month] += 1;
            
            if(userAccount.getDeactivationDate() != null) {
                deactivationDate = Calendar.getInstance();
                deactivationDate.setTime(userAccount.getDeactivationDate());
                
                year = deactivationDate.get(Calendar.YEAR) - currentYear;
                month = deactivationDate.get(Calendar.MONTH);
                data[year][month] -= 1;
            }
        }
        
        ChartSeries annualSeries;
        for(int i = 0;i < 2;i++) {
            annualSeries = new ChartSeries();
            annualSeries.setLabel(String.valueOf(currentYear + i));
            for(int j = 0;j < 12;j++) {
                annualSeries.set(months[j], data[i][j]);
            }
            membershipGrowthModel.addSeries(annualSeries);
        }
        
        ChartSeries accumulatedSeries;
        int accumulated;
        for(int i = 0;i < 2;i++) {
            accumulated = 0;
            accumulatedSeries = new ChartSeries();
            accumulatedSeries.setLabel(String.valueOf(currentYear + i));
            for(int j = 0;j < 12;j++) {
                accumulated = data[i][j] + accumulated;
                accumulatedSeries.set(months[j], accumulated);
            }
            membershipCumulativeGrowthModel.addSeries(accumulatedSeries);
        }
    }
}