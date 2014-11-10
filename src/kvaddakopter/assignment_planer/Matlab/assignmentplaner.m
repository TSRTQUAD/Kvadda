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
[trajectorylength,area] = getResults( object, [],...
    trajectory, spiraltrajectory, imagelength_meter, 0 );

    
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
[trajectorylength,area] = getResults( object, [],...
    trajectory, [], imagelength_meter, 0 );


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
[trajectorylength,area] = getResults( object, nodes,...
    trajectory, [], imagelength_meter, 0 );

end


% --------------------------------------------------
% ============ Save trajectory to file =============
% --------------------------------------------------
save('../../../../trajectory.mat');
save('../../../../trajectorylength.mat');
