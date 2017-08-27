/***********************************************************************************
 *  AlgoBench is a learning aid directed at students taking the Inf2B course to
 *  better understand the theoretical concepts, experiment with algorithms using 
 *  various inputs, and interpret the results.
 * 
 *  Copyright (C) 2017  Shalom <shalomray7@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  version 2 any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ************************************************************************************
 *  This program's developed as part of MSc project, University of Edinburgh (2017)
 *  Project: AlgoBench
 *  Supervisor: Kyriakos Kalorkoti
 *  School: School of Informatics
 *  Previous Contributor(s): None
 ***********************************************************************************/

package inf2b.algobench.util;

/**
 *
 * @author Shalom
 */
public interface ITreeSubPanel {
    /* The following methods are for Tree Information (check TreeTaskSubPanel GUI)    */
    public void setTreeType(String treeType);
    public void setTreeRange(String range);
    public void setNumberOfNodes(String numNodes);
    public void setTreeHeight(String height);
    public void setTotalMemoryFootprint(String memory);
    public void setMemoryPerNode(String memoryPerNode);
    public void setPercentageCompleted(String percentage);
    
    /* The following methods are for Basic Operations */
    public void setDataElement(String dataElement);
    public void setNodeLevel(String nodeLevel);
    public void setInsertionTimeTaken(String timeTaken);
    public void setSearchTimeTaken(String timeTaken);
    public void setDeletionTimeTaken(String timeTaken);
    public void setMemoryUsageOp(String memory);
}