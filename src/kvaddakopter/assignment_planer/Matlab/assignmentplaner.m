%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                        Missionplaner Version 1.0                        %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Input:
% mission         - mission type
% object          - mission specific data i.e. coordinates etz.
%
% Output:
% trajectory      - trajectory containing coordinates
% 
% The input object can contain different values depending on the mission.
% For areacoverage the coordinates are specified by both areas and
% forbidden areas.

% --------------------------------------------------
% ================== Load object ===================
% --------------------------------------------------
load('../../../../object.mat');
object = struct('mission',mission,'targetcoordinate',targetcoordinate,...
    'height',height,'radius',radius);

% --------------------------------------------------
% ================ Camera Coverage =================
% --------------------------------------------------
imageangle = pi/3;
imagelength_meter = 2*object.height*tan(imageangle/2)/sqrt(2);
imagelength = imagelength_meter/1.09e+05; % m -> latlon
cameracoverage = imagelength^2; % cameracoverage
ppa = 1/cameracoverage; % nr of nodes per square latlon

if strcmp(object.mission,'areacoverage')
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                  Area Coverage                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ================= Place nodes ====================
% Create nodes within the polygon
nodes = getPolygonGrid(object,ppa);

% =============== Find costmatrix ==================
% Create cost matrixes, 3 different fly patterns
tmpcostmat = getCostMatrix(nodes,object);

% =============== Find trajectory ==================
rawtrajectory = getTrajectory(tmpcostmat,nodes,object.startpoint);

% ============ Interpolate trajectory ==============
% Interpolate using parametric splines, the first argument determines the
% nr of nodes to interpolate between each nodpair in the trajectory.
trajectory = interparc(2e3,rawtrajectory(:,1),rawtrajectory(:,2),'spline');

% =============== Present results ==================
% plotResults( object, nodes, trajectory)

elseif strcmp(object.mission,'coordinatesearch')
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                Coordinate Search                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ======= Draw trajectory around coordinate ========
rotations = object.radius/(imagelength_meter/2);
th = transpose(0:pi/50:rotations*2*pi);
spanlat = 0.5*imagelength_meter/(2*pi*1.1119e+05);
spanlon = 0.5*imagelength_meter/(2*pi*5.8924e+04);
span = imagelength/(2*2*pi*rotations);
trajectory = [spanlat*th.*cos(th) spanlon*th.*sin(th)]...
    + repmat(object.targetcoordinate,size(th));

% =============== Present results ==================
% radiuslength = lldistkm(trajectory(1,:),trajectory(end,:))*1000;
% boundingcircle = [2*pi*spanlat*rotations*sin(0:0.01:2*pi)'...
%     2*pi*spanlon*rotations*cos(0:0.01:2*pi)']...
%     + repmat(object.targetcoordinate,length(0:0.01:2*pi),1);
% figure('Name','Coordinate Search','Numbertitle','off'); clf; hold on
% plot(trajectory(:,2), trajectory(:,1),'r')
% h1 = plot(boundingcircle(:,2),boundingcircle(:,1),'k');
% h2 = plot([trajectory(1,2) trajectory(end,2)],...
%     [trajectory(1,1) trajectory(end,1)], 'm--');
% legend([h1,h2],'Bounding circle',['Radius: ' num2str(radiuslength) 'm']);
% plot_google_map; hold off

    
elseif strcmp(object.mission,'linesearch')
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                   Line Search                    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ============ Interpolate trajectory ==============
rawtrajectory = object.targetcoordinates;
% Interpolate using parametric splines, the first argument determines the
% nr of nodes to interpolate between each nodpair in the trajectory.
trajectory = interparc(2e3,rawtrajectory(:,1),rawtrajectory(:,2),'spline');

% =============== Present results ==================
% figure('Name','Line Search','Numbertitle','off'); clf; hold on
% plot(object.targetcoordinates(:,2),object.targetcoordinates(:,1),'k.');
% plot(trajectory(:,2), trajectory(:,1),'r');
% plot_google_map; hold off

end


% --------------------------------------------------
% ============ Save trajectory to file =============
% --------------------------------------------------
save('../../../../trajectory.mat');
