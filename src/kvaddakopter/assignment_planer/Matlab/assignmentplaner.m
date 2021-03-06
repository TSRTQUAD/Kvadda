%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                        Missionplaner Version 1.0                        %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Input:
% object            - mission specific data i.e. coordinates etz.
%
% Output:
% trajectory        - trajectory containing coordinates
% time              - total time for mission [min]
% coveragearea      - coverage area [m^2]
% velocity          - reference vector containing velocities
% 
% The input object can contain different values depending on the mission.
% For areacoverage the coordinates are specified by both areas and
% forbidden areas.
%
% Hardcoded values:
% main              - Camera propterties and maximum pointdistance
% getResults        - Arial velocities
% DouglasPeucker    - Epsilon describing minimum prependicular distance
%                     between two points and the evaluated one.
% interpolation     - Number of points to be set between each nodpair
% lldistkm          - Earth radius

% NOTE: At the moment there are two trajectorys, one to be plotted in full
% size and one used by the controller. The id� is to use Douglas Peuckers
% algoritm on the full size trajectory. This is changed due to that the
% constant velocity controller does not work for this pointintervall. So
% for now on the trajectory is scaled down in number of points with a
% maximum distance that are set manually. This configuration is implemented
% for all modes. This means that if the trajectory are to be used with a
% reference-vector which is the future plan the implementation in this main
% script has to be edited so that the last check are done on the
% fullsizetrajectory instead of the rawtrajectory. This change also have to
% be done in the file getTrajectory.m in order for it to work in the mode
% area coverage.

% --------------------------------------------------
% ================== Load object ===================
% --------------------------------------------------
load('Data/object.mat');
object = struct('mission',mission,'startcoordinate',startcoordinate,...
    'height',height,'radius',radius);
object.area = area; object.forbiddenarea = forbiddenarea;


% --------------------------------------------------
% ================ Camera Coverage =================
% --------------------------------------------------
imageangle = pi/3;
imagelength_meter = 2*object.height*tan(imageangle/2)/sqrt(2);
imagelength = imagelength_meter/1.09e+05;           % m -> latlon
LAT = 1.111949266445575e+05/imagelength_meter;      % Latitude
LON = 58.923795838568971e+03/imagelength_meter;     % Longitud

% Hardcoded value to get maximum 4m in distance between points
object.pointdistance = 4;                           % [m]



if object.mission == 1
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                Coordinate Search                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ======= Draw trajectory around coordinate ========
spiraltrajectory = []; pointsinbetween = [];
for ii = 1:length(area);
    rotations = object.radius(ii)/(imagelength_meter/2);
    th = transpose(2*pi:pi/100:rotations*2*pi);
    spanlat = 0.5*imagelength_meter/(2*pi*1.1119e+05);
    spanlon = 0.5*imagelength_meter/(2*pi*5.8924e+04);
    span = imagelength/(2*2*pi*rotations);
    if ii == 1
        spiraltrajectory = [spanlat*th.*cos(th) spanlon*th.*sin(th)]...
            + repmat(object.area{ii},size(th));
    else
        currentspiral = [spanlat*th.*cos(th) spanlon*th.*sin(th)]...
            + repmat(object.area{ii},size(th));
        pointsinbetween = interparc(10,[spiraltrajectory(end,1) ...
            currentspiral(1,1)],[spiraltrajectory(end,2) ...
            currentspiral(1,2)],'linear');
        spiraltrajectory = [spiraltrajectory;...
            pointsinbetween(3:end-3,:); currentspiral];
    end
end
% Add a path to and from the spirals
rawtrajectory = getStartEndPath(object.startcoordinate, spiraltrajectory);

% Interpolate using parametric splines, the first argument determines the
% nr of nodes to interpolate between each nodpair in the trajectory.
trajectoryfullsize = interparc(5e2,rawtrajectory(:,1),...
    rawtrajectory(:,2),'spline');

% ================= Last check =====================
% Search for points in forbidden areas and put them on the edge
trajectoryfullsize = lastCheck( trajectoryfullsize, object );
trajectory = lastCheck( rawtrajectory, object );

% trajectory = DouglasPeucker(trajectory);

% =============== Present results ==================
[trajectorylength,coveragearea,time,velocity] = getResults( object, [],...
    trajectory, spiraltrajectory, imagelength_meter, 0 );

    
elseif object.mission == 2
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                   Line Search                    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ============ Interpolate trajectory ==============
lines = [];
for ii = 1:length(object.area)
    lines = [lines; object.area{ii}(:,:)];
end
rawtrajectory = getStartEndPath(object.startcoordinate, lines);
% Interpolate using parametric splines, the first argument determines the
% nr of nodes to interpolate between each nodpair in the trajectory.
trajectoryfullsize = interparc(5e2,rawtrajectory(:,1),...
    rawtrajectory(:,2),'spline');

% ================= Last check =====================
% Search for points in forbidden areas and put them on the edge
trajectoryfullsize = lastCheck( trajectoryfullsize, object );
trajectory = lastCheck( rawtrajectory, object );

% trajectory = DouglasPeucker(trajectory);

% =============== Present results ==================
[trajectorylength,coveragearea,time,velocity] = getResults( object, [],...
    trajectory, [], imagelength_meter, 0 );


elseif object.mission == 3
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                  Area Coverage                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ================= Place nodes ====================
% Create nodes within the polygon
nodes = getPolygonGrid(object,LAT,LON);

% =============== Find costmatrix ==================
% Create cost matrixes, 3 different fly patterns
tmpcostmat = getCostMatrix(nodes,object);

% =============== Find trajectory ==================
% The interpolation and Douglas Peucker algoritms are run inside.
[trajectory,trajectoryfullsize] = getTrajectory(object, tmpcostmat,...
    nodes, object.startcoordinate);

% =============== Present results ==================
[trajectorylength,coveragearea,time,velocity] = getResults( object,...
    nodes, trajectory, [], imagelength_meter, 0 );

end


% --------------------------------------------------
% ============== Save results to file ==============
% --------------------------------------------------
save('Data/results.mat','trajectory','trajectoryfullsize',...
    'trajectorylength','coveragearea','time','velocity');
