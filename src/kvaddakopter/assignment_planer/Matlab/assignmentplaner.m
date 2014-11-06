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
object = struct('mission',mission,'startcoordinate',startcoordinate,...
    'height',height,'radius',radius);
object.area = area; object.forbiddenarea = forbiddenarea;


% --------------------------------------------------
% ================ Camera Coverage =================
% --------------------------------------------------
imageangle = pi/3;
imagelength_meter = 2*object.height*tan(imageangle/2)/sqrt(2);
imagelength = imagelength_meter/1.09e+05; % m -> latlon
cameracoverage = imagelength^2; % cameracoverage
ppa = 1/cameracoverage; % nr of nodes per square latlon

if object.mission == 1
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                Coordinate Search                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ======= Draw trajectory around coordinate ========
rotations = object.radius/(imagelength_meter/2);
th = transpose(0:pi/50:rotations*2*pi);
spanlat = 0.5*imagelength_meter/(2*pi*1.1119e+05);
spanlon = 0.5*imagelength_meter/(2*pi*5.8924e+04);
span = imagelength/(2*2*pi*rotations);
spiraltrajectory = [spanlat*th.*cos(th) spanlon*th.*sin(th)]...
    + repmat(object.area{1},size(th));
% Add a path to and from the spiral
rawtrajectory = getStartEndPath(object.startcoordinate, spiraltrajectory);
% Interpolate using parametric splines, the first argument determines the
% nr of nodes to interpolate between each nodpair in the trajectory.
trajectory = interparc(2e3,rawtrajectory(:,1),rawtrajectory(:,2),'spline');

% =============== Present results ==================
% radiuslength = lldistkm(spiraltrajectory(1,:),spiraltrajectory(end,:))*1e3;
% boundingcircle = [2*pi*spanlat*rotations*sin(0:0.01:2*pi)'...
%     2*pi*spanlon*rotations*cos(0:0.01:2*pi)']...
%     + repmat(object.area{1},length(0:0.01:2*pi),1);
% figure('Name','Coordinate Search','Numbertitle','off'); clf; hold on
% plot(trajectory(:,2), trajectory(:,1),'r')
% h1 = plot(boundingcircle(:,2),boundingcircle(:,1),'k');
% h2 = plot([trajectory(1,2) trajectory(end,2)],...
%     [trajectory(1,1) trajectory(end,1)], 'm--');
% legend([h1,h2],'Bounding circle',['Radius: ' num2str(radiuslength) 'm']);
% plot_google_map; hold off

    
elseif object.mission == 2
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                   Line Search                    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% ============ Interpolate trajectory ==============
rawtrajectory = getStartEndPath(object.startcoordinate, object.area{1});
% Interpolate using parametric splines, the first argument determines the
% nr of nodes to interpolate between each nodpair in the trajectory.
trajectory = interparc(2e3,rawtrajectory(:,1),rawtrajectory(:,2),'spline');

% =============== Present results ==================
% figure('Name','Line Search','Numbertitle','off'); clf; hold on
% plot(object.area{1}(:,2),object.area{1}(:,1),'k.');
% plot(trajectory(:,2), trajectory(:,1),'r');
% plot_google_map; hold off


elseif object.mission == 3
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
rawtrajectory = getTrajectory(tmpcostmat,nodes,object.startcoordinate);

% ============ Interpolate trajectory ==============
% Interpolate using parametric splines, the first argument determines the
% nr of nodes to interpolate between each nodpair in the trajectory.
trajectory = interparc(2e3,rawtrajectory(:,1),rawtrajectory(:,2),'spline');

% =============== Present results ==================
% plotResults( object, nodes, trajectory)

end


% --------------------------------------------------
% =============== Trajectory length ================
% --------------------------------------------------
% Calculate the total trajectory length
trajectorylength = 0;
for ii = 2:size(trajectory,1)
    trajectorylength = trajectorylength...
        +lldistkm(trajectory(ii-1,:),trajectory(ii,:))*1e3;
end

% --------------------------------------------------
% ============ Save trajectory to file =============
% --------------------------------------------------
save('../../../../trajectory.mat');
save('../../../../trajectorylength.mat');
