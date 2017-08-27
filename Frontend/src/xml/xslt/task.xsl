<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <xsl:template match="/">
    	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    		<fo:layout-master-set>
    			<fo:simple-page-master master-name="A4-portrait"
    								   page-height="29.7cm"
    								   page-width="21.0cm"
    								   margin="1cm">
    				
    				<fo:region-body/>
                    <fo:region-before/>
                    <fo:region-after extent="1cm" overflow="hidden" />
                
    			</fo:simple-page-master>
    		</fo:layout-master-set>

    		<fo:page-sequence master-reference="A4-portrait"
    						  initial-page-number="1">

                <!-- Page number -->
                <fo:static-content flow-name="xsl-region-after">
                    <fo:block font-size="12.0pt" font-family="sans-serif"
                        padding-after="2.0pt" space-before="2.0pt" text-align="center">
                        <fo:page-number />
                    </fo:block>
                </fo:static-content>

                <!-- body template -->
                 <fo:flow flow-name="xsl-region-body">
                    <xsl:apply-templates select="data" />
                </fo:flow>
    		</fo:page-sequence>

    	</fo:root>
    </xsl:template>

    <xsl:template match="data">

		<!-- Task Title -->
		<fo:block font-size="20.0pt" font-family="serif" font-weight="bold"
		          padding-after="10.0pt" text-align="center"
		          border-bottom-style="solid" border-bottom-color="#4c4cff" border-bottom-width="1.0pt">    
		    <xsl:value-of select="task-name" />
		    <!--<xsl:text>Task</xsl:text>-->
        </fo:block>
        
        <!-- Adjacent Tables for Execution progress detail and input parameters -->
        <fo:table table-layout="fixed" width="100%" border-bottom-width="1.3pt" border-bottom-style="solid" border-bottom-color="#4c4cff" >
        	<fo:table-column column-number="1" column-width="50%"/>
            <fo:table-column column-number="2" column-width="50%"/>
            
            <fo:table-body>
                <fo:table-row>
        			<fo:table-cell>

                        <!-- Table for Execution Progress Detail -->
        				<fo:block padding-before="10.0pt" font-size="14pt" font-family="sans-serif" border-right-style="solid" border-width="1.0pt" border-color="#4c4cff">
        					<fo:block font-weight="bold">&#160;&#160;Execution Progress Details</fo:block>
        					<fo:block padding-before="15.0pt" font-size="10pt" font-family="sans-serif" padding-start="15.0pt">
        						<fo:table table-layout="fixed" width="100%">
                                	<fo:table-column column-number="1" column-width="50%"/>
        					    	<fo:table-column column-number="2" column-width="50%"/>	
        							<fo:table-body>

        							<!-- Current Input Size -->
        							<fo:table-row>
        								<fo:table-cell padding-start="20.0pt"><fo:block>Current Input Size:</fo:block></fo:table-cell>
        								<fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="input-size"/></fo:block></fo:table-cell>
        							</fo:table-row>

        							<!-- Memory footprint -->
        							<fo:table-row>
        								<fo:table-cell padding-start="20.0pt"><fo:block>Memory Footprint:</fo:block></fo:table-cell>
        								<fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="memory-footprint"/></fo:block></fo:table-cell>
        							</fo:table-row>


        							<!-- Scheduled Tasks -->
        							<fo:table-row>
        								<fo:table-cell padding-start="20.0pt"><fo:block>Scheduled Tasks:</fo:block></fo:table-cell>
        								<fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="scheduled-tasks"/></fo:block></fo:table-cell>
        							</fo:table-row>

        							<!-- Completed Tasks -->
        							<fo:table-row>
        								<fo:table-cell padding-start="20.0pt"><fo:block>Completed Tasks:</fo:block></fo:table-cell>
        								<fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="completed-tasks"/></fo:block></fo:table-cell>
        							</fo:table-row>

        							<!-- Percentage Completion -->
        							<fo:table-row>
        								<fo:table-cell padding-start="20.0pt"><fo:block>Percentage Completion:</fo:block></fo:table-cell>
        								<fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="percentage-completion"/></fo:block></fo:table-cell>
        							</fo:table-row>

        							<!-- Max Recursion Depth -->
        							<fo:table-row>
        								<fo:table-cell padding-start="20.0pt"><fo:block>Max. Recursion Depth:</fo:block></fo:table-cell>
        								<fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="max-recursion-depth"/></fo:block></fo:table-cell>
        							</fo:table-row>

                                    <!-- empty line -->
        							<fo:table-row><fo:table-cell><fo:block><fo:leader /></fo:block></fo:table-cell></fo:table-row>

        							</fo:table-body>
        						</fo:table>
        					</fo:block>
        				</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                        <!-- Table for Input Parameters -->
                        <fo:block padding-before="10.0pt" font-size="14pt" font-family="sans-serif">
                            <fo:block font-weight="bold">&#160;&#160;Input Parameters</fo:block>
                            <fo:block padding-before="15.0pt" font-size="10pt" font-family="sans-serif">
                            	<fo:table table-layout="fixed" width="100%">
                                	<fo:table-column column-number="1" column-width="50%"/>
                                	<fo:table-column column-number="2" column-width="50%"/> 

                                	<fo:table-body>

                                    <!-- Initial Size -->
                                    <fo:table-row>
                                        <fo:table-cell padding-start="20.0pt"><fo:block>Initial Size:</fo:block></fo:table-cell>
                                        <fo:table-cell padding-start="20.0pt" ><fo:block><xsl:value-of select="initial-size"/></fo:block></fo:table-cell>
                                    </fo:table-row>

                                    <!-- Final Size -->
                                    <fo:table-row>
                                        <fo:table-cell padding-start="20.0pt"><fo:block>Final Size:</fo:block></fo:table-cell>
                                        <fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="final-size"/></fo:block></fo:table-cell>
                                    </fo:table-row>


                                    <!-- Step Size -->
                                    <fo:table-row>
                                        <fo:table-cell padding-start="20.0pt"><fo:block>Step Size:</fo:block></fo:table-cell>
                                        <fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="step-size"/></fo:block></fo:table-cell>
                                    </fo:table-row>

                                    <!-- Input Range -->
                                    <fo:table-row>
                                        <fo:table-cell padding-start="20.0pt"><fo:block>Input Range:</fo:block></fo:table-cell>
                                        <fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="input-range"/></fo:block></fo:table-cell>
                                    </fo:table-row>

                                    <!-- Input Distribution -->
                                    <fo:table-row>
                                        <fo:table-cell padding-start="20.0pt"><fo:block>Input Distribution:</fo:block></fo:table-cell>
                                        <fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="input-distribution"/></fo:block></fo:table-cell>
                                    </fo:table-row>

                                    <!-- Pivot Position -->
                                    <fo:table-row>
                                        <fo:table-cell padding-start="20.0pt"><fo:block>Pivot Position:</fo:block></fo:table-cell>
                                        <fo:table-cell padding-start="20.0pt"><fo:block><xsl:value-of select="pivot-position"/></fo:block></fo:table-cell>
                                    </fo:table-row>

                                    <!-- empty line -->
        							<fo:table-row><fo:table-cell><fo:block><fo:leader /></fo:block></fo:table-cell></fo:table-row>

                                	</fo:table-body>
                                
                            	</fo:table>
                            </fo:block>
                        </fo:block>
        			</fo:table-cell>
        		</fo:table-row>
        	</fo:table-body>
        </fo:table>

        <!-- Runtime image -->
        <fo:block font-size="14.0pt" font-weight="bold" padding-before="10.0pt">&#160;&#160;Time Complexity Chart</fo:block>
        <fo:block><fo:leader /></fo:block> <!-- empty line -->
        <!-- image -->
        <fo:block border-bottom-width="1.3pt" border-bottom-style="solid" border-bottom-color="#4c4cff">
        	<fo:external-graphic src="images/LineChartquicksort_547.jpg" content-height="145mm" content-width="180mm" display-align="center" text-align="center" border="solid 0.1pt"/>
        </fo:block>

        <!-- Notes, if any -->
        <fo:block font-size="14.0pt" font-weight="bold" padding-before="10.0pt">&#160;&#160;Notes</fo:block>
        <fo:block><fo:leader /></fo:block> <!-- empty line -->
        <fo:block>&#160;&#160;&#160;&#160; <xsl:value-of select="notes"/></fo:block>
    </xsl:template>
</xsl:stylesheet>